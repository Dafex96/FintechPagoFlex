package cl.duoc.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duoc.model.AvisoAnticipacion
import cl.duoc.model.CambiosPerfil
import cl.duoc.model.Compromiso
import cl.duoc.model.Comuna
import cl.duoc.model.EstadoCompromisoId
import cl.duoc.model.NuevoCompromisoEmpresa
import cl.duoc.model.NuevoUsuario
import cl.duoc.model.Pago
import cl.duoc.model.Reporte
import cl.duoc.model.ResumenCobranza
import cl.duoc.model.ResumenInicioUsuario
import cl.duoc.model.RolCodigo
import cl.duoc.model.Usuario
import cl.duoc.util.Fechas
import cl.duoc.util.Rut
import cl.duoc.util.Seguridad
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

        private const val SQL_SELECT_COMPROMISO =
            "SELECT C.ID_COMPROMISO, C.FOLIO, C.ID_USUARIO_FINAL, C.ID_EMPRESA, " +
                    "E.NOMBRE_FANTASIA AS empresa_nombre, E.TELEFONO_ATENCION AS empresa_telefono, " +
                    "E.CORREO_ATENCION AS empresa_correo, C.CONCEPTO, C.NUMERO_CUOTA, " +
                    "C.TOTAL_CUOTAS, C.MONTO, C.FECHA_VENCIMIENTO, C.ID_ESTADO_COMPROMISO, " +
                    "EC.NOMBRE AS estado_nombre, C.RECARGO_APLICADO, C.FECHA_CAMBIO_ESTADO, C.FECHA_REGISTRO " +
                    "FROM COMPROMISO C " +
                    "INNER JOIN EMPRESA_CLIENTE E ON C.ID_EMPRESA = E.ID_EMPRESA " +
                    "INNER JOIN ESTADO_COMPROMISO EC ON C.ID_ESTADO_COMPROMISO = EC.ID_ESTADO_COMPROMISO"

        private const val SQL_SELECT_PAGO =
            "SELECT P.ID_PAGO, P.NUMERO_COMPROBANTE, P.ID_COMPROMISO, P.FECHA_HORA, " +
                    "P.MONTO_PAGADO, P.ID_CANAL, CA.NOMBRE AS canal_nombre, " +
                    "P.ID_MEDIO_PAGO, MP.NOMBRE AS medio_pago_nombre, " +
                    "E.NOMBRE_FANTASIA AS empresa_nombre, C.CONCEPTO, C.FOLIO AS folio_compromiso " +
                    "FROM PAGO P " +
                    "INNER JOIN CANAL CA ON P.ID_CANAL = CA.ID_CANAL " +
                    "INNER JOIN MEDIO_PAGO MP ON P.ID_MEDIO_PAGO = MP.ID_MEDIO_PAGO " +
                    "INNER JOIN COMPROMISO C ON P.ID_COMPROMISO = C.ID_COMPROMISO " +
                    "INNER JOIN EMPRESA_CLIENTE E ON C.ID_EMPRESA = E.ID_EMPRESA"

        private const val SQL_SELECT_REPORTE =
            "SELECT R.ID_REPORTE, R.NUMERO, R.ID_COMPROMISO, C.FOLIO AS folio_compromiso, " +
                    "E.NOMBRE_FANTASIA AS empresa_nombre, C.CONCEPTO, R.ID_MOTIVO_REPORTE, " +
                    "MR.NOMBRE AS motivo_nombre, R.COMENTARIO, R.FECHA_CREACION, " +
                    "R.ID_ESTADO_REPORTE, ER.NOMBRE AS estado_nombre, R.ID_AGENTE, " +
                    "AG.NOMBRES || ' ' || AG.APELLIDOS AS agente_nombre, R.FECHA_TOMA, " +
                    "R.RESPUESTA, R.FECHA_RESOLUCION " +
                    "FROM REPORTE R " +
                    "INNER JOIN COMPROMISO C ON R.ID_COMPROMISO = C.ID_COMPROMISO " +
                    "INNER JOIN EMPRESA_CLIENTE E ON C.ID_EMPRESA = E.ID_EMPRESA " +
                    "INNER JOIN MOTIVO_REPORTE MR ON R.ID_MOTIVO_REPORTE = MR.ID_MOTIVO_REPORTE " +
                    "INNER JOIN ESTADO_REPORTE ER ON R.ID_ESTADO_REPORTE = ER.ID_ESTADO_REPORTE " +
                    "LEFT JOIN USUARIO AG ON R.ID_AGENTE = AG.ID_USUARIO"
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

    fun buscarUsuarioPorRutOCorreo(identificador: String): Usuario? {
        val limpio = identificador.trim()
        val rutNormalizado = if (Rut.esValido(limpio)) Rut.normalizar(limpio) else limpio
        return consultarUsuarios(
            "$SQL_SELECT_USUARIO WHERE U.RUT = ? OR LOWER(U.CORREO) = LOWER(?)",
            arrayOf(rutNormalizado, limpio)
        ).firstOrNull()
    }

    fun actualizarContrasena(idUsuario: Long, nuevaContrasenaHash: String): Int {
        val valores = ContentValues().apply {
            put("contrasena_hash", nuevaContrasenaHash)
        }
        return writableDatabase.update("usuario", valores, "id_usuario = ?", arrayOf(idUsuario.toString()))
    }

    fun listarCompromisosUsuario(idUsuario: Long, filtroEstado: Long? = null): List<Compromiso> {
        val sql = StringBuilder("$SQL_SELECT_COMPROMISO WHERE C.ID_USUARIO_FINAL = ?")
        val args = mutableListOf(idUsuario.toString())
        if (filtroEstado != null) {
            sql.append(" AND C.ID_ESTADO_COMPROMISO = ?")
            args.add(filtroEstado.toString())
        }
        sql.append(" ORDER BY C.FECHA_VENCIMIENTO ASC")
        return consultarCompromisos(sql.toString(), args.toTypedArray())
    }

    fun buscarCompromisoPorId(idCompromiso: Long): Compromiso? =
        consultarCompromisos("$SQL_SELECT_COMPROMISO WHERE C.ID_COMPROMISO = ?", arrayOf(idCompromiso.toString()))
            .firstOrNull()

    fun obtenerResumenInicioUsuario(idUsuario: Long): ResumenInicioUsuario {
        val compromisos = listarCompromisosUsuario(idUsuario)
        val pendientes = compromisos.filter { it.esPendiente }
        val vencidos = compromisos.filter { it.esVencido }
        val total = (pendientes + vencidos).sumOf { it.montoTotal }
        val proximo = (pendientes + vencidos).minByOrNull { it.fechaVencimiento }

        return ResumenInicioUsuario(
            totalMes = total,
            proximoCompromiso = proximo,
            cantidadVencidos = vencidos.size,
            compromisosPendientes = pendientes,
            compromisosVencidos = vencidos
        )
    }

    fun registrarPago(idCompromiso: Long, idMedioPago: Long, idCanal: Long = 1): Pago {
        val compromiso = buscarCompromisoPorId(idCompromiso) ?: error("Compromiso no encontrado")
        val db = writableDatabase
        db.beginTransaction()
        try {
            val numComprobante = "CMP-${(10000..99999).random()}"
            val ahoraIso = Fechas.ahoraIso()

            val valoresPago = ContentValues().apply {
                put("numero_comprobante", numComprobante)
                put("id_compromiso", idCompromiso)
                put("fecha_hora", ahoraIso)
                put("monto_pagado", compromiso.montoTotal)
                put("id_canal", idCanal)
                put("id_medio_pago", idMedioPago)
            }
            val idPago = db.insertOrThrow("pago", null, valoresPago)

            val valoresCompromiso = ContentValues().apply {
                put("id_estado_compromiso", EstadoCompromisoId.PAGADO)
                put("fecha_cambio_estado", ahoraIso)
            }
            db.update("compromiso", valoresCompromiso, "id_compromiso = ?", arrayOf(idCompromiso.toString()))

            db.setTransactionSuccessful()
            return obtenerPagoPorId(idPago) ?: error("No se pudo crear el pago")
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerPagoPorId(idPago: Long): Pago? =
        consultarPagos("$SQL_SELECT_PAGO WHERE P.ID_PAGO = ?", arrayOf(idPago.toString())).firstOrNull()

    fun obtenerPagoPorCompromiso(idCompromiso: Long): Pago? =
        consultarPagos("$SQL_SELECT_PAGO WHERE P.ID_COMPROMISO = ?", arrayOf(idCompromiso.toString())).firstOrNull()

    fun listarPagosUsuario(idUsuario: Long): List<Pago> =
        consultarPagos(
            "$SQL_SELECT_PAGO WHERE C.ID_USUARIO_FINAL = ? ORDER BY P.FECHA_HORA DESC",
            arrayOf(idUsuario.toString())
        )

    fun registrarReporte(idCompromiso: Long, idMotivo: Long, comentario: String?): Reporte {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val numReporte = "REP-${(1000..9999).random()}"
            val ahoraIso = Fechas.ahoraIso()

            val valoresReporte = ContentValues().apply {
                put("numero", numReporte)
                put("id_compromiso", idCompromiso)
                put("id_motivo_reporte", idMotivo)
                if (!comentario.isNullOrBlank()) put("comentario", comentario) else putNull("comentario")
                put("fecha_creacion", ahoraIso)
                put("id_estado_reporte", 1)
            }
            val idReporte = db.insertOrThrow("reporte", null, valoresReporte)

            val valoresCompromiso = ContentValues().apply {
                put("id_estado_compromiso", EstadoCompromisoId.EN_REVISION)
                put("fecha_cambio_estado", ahoraIso)
            }
            db.update("compromiso", valoresCompromiso, "id_compromiso = ?", arrayOf(idCompromiso.toString()))

            db.setTransactionSuccessful()
            return consultarReportes("$SQL_SELECT_REPORTE WHERE R.ID_REPORTE = ?", arrayOf(idReporte.toString())).first()
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerReportePorCompromiso(idCompromiso: Long): Reporte? =
        consultarReportes("$SQL_SELECT_REPORTE WHERE R.ID_COMPROMISO = ?", arrayOf(idCompromiso.toString())).firstOrNull()

    fun listarReportesUsuario(idUsuario: Long): List<Reporte> =
        consultarReportes(
            "$SQL_SELECT_REPORTE WHERE C.ID_USUARIO_FINAL = ? ORDER BY R.FECHA_CREACION DESC",
            arrayOf(idUsuario.toString())
        )

    fun actualizarAvisosUsuario(idUsuario: Long, activos: Boolean, diasAnticipacion: Int): Boolean {
        val valores = ContentValues().apply {
            put("avisos_activos", if (activos) 1 else 0)
            put("dias_anticipacion", diasAnticipacion)
        }
        return writableDatabase.update("usuario", valores, "id_usuario = ?", arrayOf(idUsuario.toString())) > 0
    }

    fun listarCompromisosEmpresa(idEmpresa: Long, filtroEstado: Long? = null): List<Compromiso> {
        val sql = StringBuilder("$SQL_SELECT_COMPROMISO WHERE C.ID_EMPRESA = ?")
        val args = mutableListOf(idEmpresa.toString())
        if (filtroEstado != null) {
            sql.append(" AND C.ID_ESTADO_COMPROMISO = ?")
            args.add(filtroEstado.toString())
        }
        sql.append(" ORDER BY C.FECHA_VENCIMIENTO ASC")
        return consultarCompromisos(sql.toString(), args.toTypedArray())
    }

    fun obtenerResumenCobranzaEmpresa(idEmpresa: Long): ResumenCobranza {
        val compromisos = listarCompromisosEmpresa(idEmpresa)
        var recaudado = 0
        var pendiente = 0
        var vencido = 0
        var pagadosCount = 0
        var pendientesCount = 0
        var vencidosCount = 0
        var anuladosCount = 0

        val empresaNombre = compromisos.firstOrNull()?.empresaNombre ?: "Empresa"

        compromisos.forEach { comp ->
            when {
                comp.esPagado -> {
                    recaudado += comp.montoTotal
                    pagadosCount++
                }
                comp.esPendiente -> {
                    pendiente += comp.montoTotal
                    pendientesCount++
                }
                comp.esVencido -> {
                    vencido += comp.montoTotal
                    vencidosCount++
                }
                comp.esAnulado -> {
                    anuladosCount++
                }
            }
        }

        return ResumenCobranza(
            empresaNombre = empresaNombre,
            montoRecaudado = recaudado,
            montoPendiente = pendiente,
            montoVencido = vencido,
            totalCompromisos = compromisos.size,
            pagadosCount = pagadosCount,
            pendientesCount = pendientesCount,
            vencidosCount = vencidosCount,
            anuladosCount = anuladosCount
        )
    }

    fun registrarCompromisoEmpresa(idEmpresa: Long, idEjecutivo: Long, datos: NuevoCompromisoEmpresa): String {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val rutNormalizado = Rut.normalizar(datos.rutPersona)
            var idUsuarioFinal = readableDatabase.rawQuery(
                "SELECT ID_USUARIO FROM USUARIO WHERE RUT = ?",
                arrayOf(rutNormalizado)
            ).use { cursor -> if (cursor.moveToFirst()) cursor.getLong(0) else -1L }

            if (idUsuarioFinal == -1L) {
                val valoresUsuario = ContentValues().apply {
                    put("id_rol", idDeRol(RolCodigo.USUARIO_FINAL))
                    put("rut", rutNormalizado)
                    put("nombres", datos.nombresPersona.trim())
                    put("apellidos", datos.apellidosPersona.trim())
                    put("correo", datos.correoPersona.trim().lowercase())
                    put("contrasena_hash", Seguridad.hash("PagoFlex2026"))
                    if (datos.telefonoPersona.isNotBlank()) put("telefono", datos.telefonoPersona.trim())
                    put("activo", 1)
                    put("fecha_creacion", Fechas.ahoraIso())
                    put("fecha_incorporacion", Fechas.hoyIso())
                    put("avisos_activos", 1)
                    put("dias_anticipacion", 3)
                }
                idUsuarioFinal = db.insertOrThrow("usuario", null, valoresUsuario)
            }

            val folio = "F-${(1000..9999).random()}"
            val ahoraIso = Fechas.ahoraIso()

            val valoresCompromiso = ContentValues().apply {
                put("folio", folio)
                put("id_usuario_final", idUsuarioFinal)
                put("id_empresa", idEmpresa)
                put("concepto", datos.concepto.trim())
                if (datos.numeroCuota.isNotBlank()) put("numero_cuota", datos.numeroCuota.toInt())
                if (datos.totalCuotas.isNotBlank()) put("total_cuotas", datos.totalCuotas.toInt())
                put("monto", datos.monto.toInt())
                put("fecha_vencimiento", datos.fechaVencimiento)
                put("id_estado_compromiso", EstadoCompromisoId.PENDIENTE)
                put("recargo_aplicado", 0)
                put("fecha_cambio_estado", ahoraIso)
                put("id_ejecutivo_registro", idEjecutivo)
                put("fecha_registro", ahoraIso)
            }
            db.insertOrThrow("compromiso", null, valoresCompromiso)

            db.setTransactionSuccessful()
            return folio
        } finally {
            db.endTransaction()
        }
    }

    fun anularCompromisoEmpresa(idCompromiso: Long, idEjecutivo: Long, idMotivo: Long, comentario: String?): Boolean {
        val compromiso = buscarCompromisoPorId(idCompromiso) ?: return false
        if (compromiso.esPagado) {
            return false
        }

        val db = writableDatabase
        db.beginTransaction()
        try {
            val ahoraIso = Fechas.ahoraIso()

            val valoresAnulacion = ContentValues().apply {
                put("id_compromiso", idCompromiso)
                put("id_ejecutivo", idEjecutivo)
                put("id_motivo_anulacion", idMotivo)
                if (!comentario.isNullOrBlank()) put("comentario", comentario) else putNull("comentario")
                put("fecha", ahoraIso)
            }
            db.insertOrThrow("anulacion", null, valoresAnulacion)

            val valoresCompromiso = ContentValues().apply {
                put("id_estado_compromiso", EstadoCompromisoId.ANULADO)
                put("fecha_cambio_estado", ahoraIso)
            }
            db.update("compromiso", valoresCompromiso, "id_compromiso = ?", arrayOf(idCompromiso.toString()))

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    private fun consultarCompromisos(sql: String, args: Array<String>?): List<Compromiso> {
        val lista = mutableListOf<Compromiso>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerCompromiso(cursor))
            }
        }
        return lista
    }

    private fun leerCompromiso(cursor: Cursor) = Compromiso(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id_compromiso")),
        folio = cursor.getString(cursor.getColumnIndexOrThrow("folio")),
        idUsuarioFinal = cursor.getLong(cursor.getColumnIndexOrThrow("id_usuario_final")),
        idEmpresa = cursor.getLong(cursor.getColumnIndexOrThrow("id_empresa")),
        empresaNombre = cursor.getString(cursor.getColumnIndexOrThrow("empresa_nombre")),
        empresaTelefono = cursor.getString(cursor.getColumnIndexOrThrow("empresa_telefono")),
        empresaCorreo = cursor.getString(cursor.getColumnIndexOrThrow("empresa_correo")),
        concepto = cursor.getString(cursor.getColumnIndexOrThrow("concepto")),
        numeroCuota = cursor.largoONulo("numero_cuota")?.toInt(),
        totalCuotas = cursor.largoONulo("total_cuotas")?.toInt(),
        monto = cursor.getInt(cursor.getColumnIndexOrThrow("monto")),
        fechaVencimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento")),
        idEstadoCompromiso = cursor.getLong(cursor.getColumnIndexOrThrow("id_estado_compromiso")),
        estadoNombre = cursor.getString(cursor.getColumnIndexOrThrow("estado_nombre")),
        recargoAplicado = cursor.getInt(cursor.getColumnIndexOrThrow("recargo_aplicado")),
        fechaCambioEstado = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cambio_estado")),
        fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
    )

    private fun consultarPagos(sql: String, args: Array<String>?): List<Pago> {
        val lista = mutableListOf<Pago>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerPago(cursor))
            }
        }
        return lista
    }

    private fun leerPago(cursor: Cursor) = Pago(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id_pago")),
        numeroComprobante = cursor.getString(cursor.getColumnIndexOrThrow("numero_comprobante")),
        idCompromiso = cursor.getLong(cursor.getColumnIndexOrThrow("id_compromiso")),
        fechaHora = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")),
        montoPagado = cursor.getInt(cursor.getColumnIndexOrThrow("monto_pagado")),
        idCanal = cursor.getLong(cursor.getColumnIndexOrThrow("id_canal")),
        canalNombre = cursor.getString(cursor.getColumnIndexOrThrow("canal_nombre")),
        idMedioPago = cursor.getLong(cursor.getColumnIndexOrThrow("id_medio_pago")),
        medioPagoNombre = cursor.getString(cursor.getColumnIndexOrThrow("medio_pago_nombre")),
        empresaNombre = cursor.getString(cursor.getColumnIndexOrThrow("empresa_nombre")),
        concepto = cursor.getString(cursor.getColumnIndexOrThrow("concepto")),
        folioCompromiso = cursor.getString(cursor.getColumnIndexOrThrow("folio_compromiso"))
    )

    private fun consultarReportes(sql: String, args: Array<String>?): List<Reporte> {
        val lista = mutableListOf<Reporte>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerReporte(cursor))
            }
        }
        return lista
    }

    private fun leerReporte(cursor: Cursor) = Reporte(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id_reporte")),
        numero = cursor.getString(cursor.getColumnIndexOrThrow("numero")),
        idCompromiso = cursor.getLong(cursor.getColumnIndexOrThrow("id_compromiso")),
        folioCompromiso = cursor.getString(cursor.getColumnIndexOrThrow("folio_compromiso")),
        empresaNombre = cursor.getString(cursor.getColumnIndexOrThrow("empresa_nombre")),
        concepto = cursor.getString(cursor.getColumnIndexOrThrow("concepto")),
        idMotivoReporte = cursor.getLong(cursor.getColumnIndexOrThrow("id_motivo_reporte")),
        motivoNombre = cursor.getString(cursor.getColumnIndexOrThrow("motivo_nombre")),
        comentario = cursor.textoONulo("comentario"),
        fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion")),
        idEstadoReporte = cursor.getLong(cursor.getColumnIndexOrThrow("id_estado_reporte")),
        estadoNombre = cursor.getString(cursor.getColumnIndexOrThrow("estado_nombre")),
        idAgente = cursor.largoONulo("id_agente"),
        agenteNombre = cursor.textoONulo("agente_nombre"),
        fechaToma = cursor.textoONulo("fecha_toma"),
        respuesta = cursor.textoONulo("respuesta"),
        fechaResolucion = cursor.textoONulo("fecha_resolucion")
    )

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