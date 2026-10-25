package cl.duoc.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duoc.model.AvisoAnticipacion
import cl.duoc.model.CambiosPerfil
import cl.duoc.model.Comuna
import cl.duoc.model.NuevoUsuario
import cl.duoc.model.RolCodigo
import cl.duoc.model.Usuario
import cl.duoc.util.Fechas
import java.text.Collator
import java.util.Locale

class PagoFlexDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "pagoflexdb"

        private const val VERSION_BD = 2

        private const val SQL_SELECT_USUARIO =
            "SELECT U.ID_USUARIO, U.RUT, U.NOMBRES, U.APELLIDOS, U.CORREO, " +
                    "U.ID_ROL, R.CODIGO AS rol_codigo, U.ACTIVO, U.ID_EMPRESA, " +
                    "U.TELEFONO AS telefono, U.ID_COMUNA AS id_comuna, C.NOMBRE AS comuna_nombre, " +
                    "U.FECHA_INCORPORACION AS fecha_incorporacion, A.NOMBRE AS area_nombre, " +
                    "E.NOMBRE_FANTASIA AS empresa_nombre, U.CARGO AS cargo, U.AVATAR AS avatar " +
                    "FROM USUARIO U INNER JOIN ROL R ON U.ID_ROL = R.ID_ROL " +
                    "LEFT JOIN COMUNA C ON U.ID_COMUNA = C.ID_COMUNA " +
                    "LEFT JOIN AREA A ON U.ID_AREA = A.ID_AREA " +
                    "LEFT JOIN EMPRESA_CLIENTE E ON U.ID_EMPRESA = E.ID_EMPRESA"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        EsquemaBd.CREAR_TABLAS.forEach { sql -> db.execSQL(sql) }
        DatosIniciales.cargar(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE USUARIO ADD COLUMN AVATAR BLOB")
        }
    }

    fun validarCredenciales(rut: String, contrasenaHash: String): Usuario? =
        consultarUsuarios(
            "$SQL_SELECT_USUARIO WHERE U.RUT = ? AND U.CONTRASENA_HASH = ?",
            arrayOf(rut, contrasenaHash)
        ).firstOrNull()

    fun buscarUsuarioPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIO WHERE U.ID_USUARIO = ?", arrayOf(id.toString()))
            .firstOrNull()

    fun listarComunas(): List<Comuna> {
        val lista = mutableListOf<Comuna>()
        readableDatabase.rawQuery("SELECT ID_COMUNA, NOMBRE FROM COMUNA", null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(Comuna(id = cursor.getLong(0), nombre = cursor.getString(1)))
            }
        }
        val collator = Collator.getInstance(Locale.forLanguageTag("es-CL"))
        return lista.sortedWith { a, b -> collator.compare(a.nombre, b.nombre) }
    }

    fun existeRut(rut: String): Boolean =
        existe("SELECT 1 FROM USUARIO WHERE RUT = ?", rut)

    fun existeCorreo(correo: String): Boolean =
        existe("SELECT 1 FROM USUARIO WHERE LOWER(CORREO) = LOWER(?)", correo)

    fun registrarUsuarioFinal(nuevo: NuevoUsuario): Long {
        val valores = ContentValues().apply {
            put("id_rol", idDeRol(RolCodigo.USUARIO_FINAL))
            put("rut", nuevo.rut)
            put("nombres", nuevo.nombres)
            put("apellidos", nuevo.apellidos)
            put("correo", nuevo.correo.lowercase())
            put("contrasena_hash", nuevo.contrasenaHash)
            if (nuevo.telefono != null) put("telefono", nuevo.telefono) else putNull("telefono")
            put("activo", 1)
            put("fecha_creacion", Fechas.ahoraIso())
            put("id_comuna", nuevo.comunaId)
            put("fecha_incorporacion", Fechas.hoyIso())
            put("avisos_activos", 1)
            put("dias_anticipacion", AvisoAnticipacion.POR_DEFECTO)
        }
        return writableDatabase.insertOrThrow("usuario", null, valores)
    }

    fun existeCorreoDeOtro(correo: String, idUsuario: Long): Boolean =
        readableDatabase.rawQuery(
            "SELECT 1 FROM USUARIO WHERE LOWER(CORREO) = LOWER(?) AND ID_USUARIO <> ?",
            arrayOf(correo, idUsuario.toString())
        ).use { cursor -> cursor.moveToFirst() }

    fun actualizarPerfil(cambios: CambiosPerfil): Int {
        val valores = ContentValues().apply {
            put("nombres", cambios.nombres)
            put("apellidos", cambios.apellidos)
            put("correo", cambios.correo.lowercase())
            if (cambios.telefono != null) put("telefono", cambios.telefono) else putNull("telefono")
            if (cambios.comunaId != null) put("id_comuna", cambios.comunaId)
        }
        return writableDatabase.update("usuario", valores, "id_usuario = ?", arrayOf(cambios.id.toString()))
    }

    fun actualizarAvatar(idUsuario: Long, avatar: ByteArray?): Int {
        val valores = ContentValues().apply {
            if (avatar != null) put("avatar", avatar) else putNull("avatar")
        }
        return writableDatabase.update("usuario", valores, "id_usuario = ?", arrayOf(idUsuario.toString()))
    }

    private fun consultarUsuarios(sql: String, args: Array<String>?): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerUsuario(cursor))
            }
        }
        return lista
    }

    private fun leerUsuario(cursor: Cursor) = Usuario(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id_usuario")),
        rut = cursor.getString(cursor.getColumnIndexOrThrow("rut")),
        nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres")),
        apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
        correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
        rolId = cursor.getLong(cursor.getColumnIndexOrThrow("id_rol")),
        rolCodigo = cursor.getString(cursor.getColumnIndexOrThrow("rol_codigo")),
        activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1,
        idEmpresa = cursor.getColumnIndexOrThrow("id_empresa").let { columna ->
            if (cursor.isNull(columna)) null else cursor.getLong(columna)
        },
        telefono = cursor.textoONulo("telefono"),
        comunaId = cursor.largoONulo("id_comuna"),
        comunaNombre = cursor.textoONulo("comuna_nombre"),
        fechaIncorporacion = cursor.textoONulo("fecha_incorporacion"),
        areaNombre = cursor.textoONulo("area_nombre"),
        empresaNombre = cursor.textoONulo("empresa_nombre"),
        cargo = cursor.textoONulo("cargo"),
        avatar = cursor.getColumnIndexOrThrow("avatar").let { columna ->
            if (cursor.isNull(columna)) null else cursor.getBlob(columna)
        }
    )

    private fun Cursor.textoONulo(columna: String): String? {
        val indice = getColumnIndexOrThrow(columna)
        return if (isNull(indice)) null else getString(indice)
    }

    private fun Cursor.largoONulo(columna: String): Long? {
        val indice = getColumnIndexOrThrow(columna)
        return if (isNull(indice)) null else getLong(indice)
    }

    private fun existe(sql: String, valor: String): Boolean =
        readableDatabase.rawQuery(sql, arrayOf(valor)).use { cursor -> cursor.moveToFirst() }

    private fun idDeRol(codigo: String): Long =
        readableDatabase.rawQuery("SELECT ID_ROL FROM ROL WHERE CODIGO = ?", arrayOf(codigo)).use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) else error("No existe el rol $codigo")
        }
}