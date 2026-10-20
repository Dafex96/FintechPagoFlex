package cl.duoc.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duoc.model.Usuario

class PagoFlexDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "pagoflexdb"

        // Sube este número cuando cambies una tabla. Mientras desarrollamos,
        // onUpgrade borra y vuelve a crear todo (ver más abajo).
        private const val VERSION_BD = 1

        // Un usuario con el nombre de su rol (JOIN con la tabla rol).
        // Ojo: NO trae contrasena_hash; la contraseña nunca sale de la base.
        private const val SQL_SELECT_USUARIO =
            "SELECT U.ID_USUARIO, U.RUT, U.NOMBRES, U.APELLIDOS, U.CORREO, " +
                    "U.ID_ROL, R.CODIGO AS ROL_CODIGO, U.ACTIVO, U.ID_EMPRESA " +
                    "FROM USUARIO U INNER JOIN ROL R ON U.ID_ROL = R.ID_ROL"
    }

    // Activa las claves foráneas (en SQLite vienen desactivadas por defecto).
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // La primera vez: crea las 22 tablas y carga los datos de prueba.
    override fun onCreate(db: SQLiteDatabase) {
        EsquemaBd.CREAR_TABLAS.forEach { sql -> db.execSQL(sql) }
        DatosIniciales.cargar(db)
    }

    // Durante el desarrollo: borra todo y empieza de cero.
    // (Cuando la app ya tenga datos reales se cambia por migraciones con ALTER TABLE,
    //  como hizo el proyecto de clases al agregar la columna avatar.)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        EsquemaBd.TABLAS.reversed().forEach { tabla -> db.execSQL("DROP TABLE IF EXISTS $tabla") }
        onCreate(db)
    }

    // ----------------------------- INICIO DE SESIÓN -----------------------------

    /**
     * Busca al usuario con ese RUT y esa contraseña (ya convertida a hash).
     * Devuelve null si no coinciden. NO revisa si está activo: eso lo decide el ViewModel.
     * @param rut            RUT normalizado: "12345678-5"
     * @param contrasenaHash resultado de Seguridad.hash(contraseña escrita)
     */
    fun validarCredenciales(rut: String, contrasenaHash: String): Usuario? =
        consultarUsuarios(
            "$SQL_SELECT_USUARIO WHERE U.RUT = ? AND U.CONTRASENA_HASH = ?",
            arrayOf(rut, contrasenaHash)
        ).firstOrNull()

    /** Para recuperar la sesión guardada (SesionPreferencias guarda solo el id). */
    fun buscarUsuarioPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIO WHERE U.ID_USUARIO = ?", arrayOf(id.toString()))
            .firstOrNull()

    // ----------------------------- AYUDAS PRIVADAS -----------------------------

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
        // id_empresa solo existe para ejecutivos: en los demás roles es NULL
        idEmpresa = cursor.getColumnIndexOrThrow("id_empresa").let { columna ->
            if (cursor.isNull(columna)) null else cursor.getLong(columna)
        }
    )
}