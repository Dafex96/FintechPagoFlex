package cl.duoc.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import cl.duoc.model.EstadoCompromisoId
import cl.duoc.model.EstadoRecomendacionId
import cl.duoc.util.Seguridad

object DatosIniciales {

    const val CONTRASENA_DEMO = "PagoFlex2026"

    private val ROLES = listOf(
        Triple("USUARIO_FINAL", "Usuario final", "Revisa sus compromisos, paga y gestiona avisos, reportes, recomendaciones y reconocimientos."),
        Triple("AGENTE", "Agente PagoFlex", "Gestiona los reportes y las recomendaciones."),
        Triple("EJECUTIVO", "Ejecutivo de empresa cliente", "Registra y anula compromisos de su empresa y revisa su cobranza."),
    )
    private val COMUNAS = listOf(
        "Maipú", "Puente Alto", "San Miguel", "La Florida", "Ñuñoa", "Quilicura", "San Bernardo",
        "Estación Central", "Santiago", "Providencia", "Las Condes", "La Reina", "Peñalolén",
        "Macul", "Recoleta", "Independencia", "Renca", "Pudahuel", "Cerrillos", "Lo Prado",
    )
    private val AREAS = listOf("Soporte", "Comercial")
    private val RUBROS = listOf(
        "Inmobiliaria", "Educación escolar", "Crédito de consumo", "Seguros", "ONG", "Club deportivo",
        "Educación superior", "Administración de arriendos", "Educación parvularia", "Administración de condominio", "Deporte", "Educación artística", "Otro",
    )
    private val ESTADOS_COMPROMISO = listOf("Pendiente", "Vencido", "Pagado", "En revisión", "Anulado")
    private val MOTIVOS_ANULACION = listOf("Repactación", "Error de registro", "Pagado directamente a la empresa", "Otro")
    private val CANALES = listOf("App", "Portal de pagos", "Agente conversacional")
    private val MEDIOS_PAGO = listOf("Tarjeta de débito", "Tarjeta de crédito", "Transferencia", "Pago simulado")
    private val MOTIVOS_REPORTE = listOf("Ya pagué por otro medio", "No reconozco este cobro", "El monto no corresponde", "Otro")
    private val ESTADOS_REPORTE = listOf("Abierto", "En gestión", "Resuelto")

    private val ESTADOS_RECOMENDACION = listOf(
        "Recibida" to 1, "Contactada" to 2, "En evaluación" to 3, "Se unió" to 4, "Descartada" to 4,
    )
    private val RECONOCIMIENTOS = listOf(
        "Primer pago a tiempo" to "Pagaste un compromiso antes o el mismo día de su vencimiento.",
        "Persona puntual" to "Acumulaste 10 pagos a tiempo en tu historial.",
        "3 meses al día" to "Pagaste a tiempo todos tus compromisos durante 3 meses seguidos.",
        "6 meses al día" to "Pagaste a tiempo todos tus compromisos durante 6 meses seguidos.",
        "Un año al día" to "Pagaste a tiempo todos tus compromisos durante 12 meses seguidos.",
        "Impulsor de PagoFlex" to "Una organización que recomendaste se unió a PagoFlex.",
    )

    private data class UsuariosFinales(val rut: String, val nombres: String, val apellidos: String, val correo: String,
                                       val telefono: String, val comuna: String, val incorporacion: String, val diasAviso: Int?)
    private data class Agentes(val rut: String, val nombres: String, val apellidos: String, val correo: String,
                               val area: String, val activo: Boolean)
    private data class Ejecutivos(val rut: String, val nombres: String, val apellidos: String, val correo: String,
                                  val telefono: String, val idEmpresa: Int, val cargo: String)
    private data class Empresas(val codigo: String, val razonSocial: String, val nombreFantasia: String, val rut: String,
                                val rubro: String, val correo: String, val telefono: String, val recargo: Int)

    private val EMPRESAS = listOf(
        Empresas("EC01", "Inmobiliaria Altos del Maipo SpA", "Altos del Maipo", "76845213-K", "Inmobiliaria", "atencion@altosmaipo.ejemplo", "+56 2 0000 0201", 8500),
        Empresas("EC02", "Corporación Educacional Bosque Nativo", "Colegio Bosque Nativo", "65132874-8", "Educación escolar", "pagos@bosquenativo.ejemplo", "+56 2 0000 0202", 5000),
        Empresas("EC03", "Crédito Andino S.A.", "Crédito Andino", "96571438-3", "Crédito de consumo", "clientes@creditoandino.ejemplo", "+56 2 0000 0203", 12000),
        Empresas("EC04", "Seguros Horizonte Sur S.A.", "Horizonte Sur", "96814205-4", "Seguros", "contacto@horizontesur.ejemplo", "+56 2 0000 0204", 3000),
        Empresas("EC05", "Fundación Manos Abiertas", "Manos Abiertas", "65247190-0", "ONG", "socios@manosabiertas.ejemplo", "+56 2 0000 0205", 0),
        Empresas("EC06", "Club Deportivo Los Halcones", "Club Los Halcones", "65398412-K", "Club deportivo", "tesoreria@loshalcones.ejemplo", "+56 2 0000 0206", 2000),
        Empresas("EC07", "Instituto Técnico Cordillera SpA", "Instituto Cordillera", "77214583-7", "Educación superior", "finanzas@itcordillera.ejemplo", "+56 2 0000 0207", 6000),
        Empresas("EC08", "Arriendos Plaza Oriente Ltda.", "Plaza Oriente", "76432871-K", "Administración de arriendos", "cobranza@plazaoriente.ejemplo", "+56 2 0000 0208", 7500),
    )

    private val USUARIOS_FINALES = listOf(
        UsuariosFinales("15834207-3", "Camila Andrea", "Rojas Peña", "crojas@correo.ejemplo", "+56 9 0000 0101", "Maipú", "2023-03-14", 3),
        UsuariosFinales("12476318-5", "Luis Alberto", "Muñoz Tapia", "lmunoz@correo.ejemplo", "+56 9 0000 0102", "Puente Alto", "2023-11-02", 1),
        UsuariosFinales("6893541-5", "Rosa Elena", "Contreras Díaz", "rcontreras@correo.ejemplo", "+56 9 0000 0103", "San Miguel", "2024-06-20", 5),
        UsuariosFinales("21045873-5", "Matías Ignacio", "Soto Vergara", "msoto@correo.ejemplo", "+56 9 0000 0104", "La Florida", "2025-03-07", null),
        UsuariosFinales("17562904-1", "Fernanda Paz", "Godoy Lagos", "fgodoy@correo.ejemplo", "+56 9 0000 0105", "Ñuñoa", "2024-01-15", 3),
        UsuariosFinales("10318265-4", "Héctor Manuel", "Pizarro Campos", "hpizarro@correo.ejemplo", "+56 9 0000 0106", "Quilicura", "2023-08-28", 3),
        UsuariosFinales("19287430-0", "Javiera Constanza", "Reyes Molina", "jreyes@correo.ejemplo", "+56 9 0000 0107", "San Bernardo", "2025-05-03", 1),
        UsuariosFinales("18704396-4", "Benjamín Esteban", "Araya Fuentes", "baraya@correo.ejemplo", "+56 9 0000 0108", "Estación Central", "2026-09-11", 3),
    )

    private val AGENTES = listOf(
        Agentes("16248317-K", "Andrés Felipe", "Lagos Meza", "alagos@pagoflex.ejemplo", "Soporte", true),
        Agentes("18352091-1", "Daniela Sofía", "Ortiz Bravo", "dortiz@pagoflex.ejemplo", "Soporte", true),
        Agentes("13907264-2", "Ricardo José", "Salinas Cáceres", "rsalinas@pagoflex.ejemplo", "Soporte", false),
        Agentes("17781435-0", "Valentina Ignacia", "Paredes Silva", "vparedes@pagoflex.ejemplo", "Comercial", true),
        Agentes("19564028-9", "Tomás Alejandro", "Vidal Rojas", "tvidal@pagoflex.ejemplo", "Comercial", true),
    )

    private val EJECUTIVOS = listOf(
        Ejecutivos("14629853-2", "Paula Andrea", "Saavedra Núñez", "psaavedra@altosmaipo.ejemplo", "+56 9 0000 0401", 1, "Ejecutiva de cobranza"),
        Ejecutivos("11853720-3", "Marco Antonio", "Fuentes Riquelme", "mfuentes@bosquenativo.ejemplo", "+56 9 0000 0402", 2, "Encargado de finanzas"),
        Ejecutivos("15370946-7", "Claudia Beatriz", "Henríquez Soto", "chenriquez@creditoandino.ejemplo", "+56 9 0000 0403", 3, "Jefa de cobranza"),
        Ejecutivos("17095482-3", "Felipe Ignacio", "Morales Tapia", "fmorales@creditoandino.ejemplo", "+56 9 0000 0404", 3, "Ejecutivo de cobranza"),
        Ejecutivos("9846217-1", "Gloria Inés", "Carrasco Vega", "gcarrasco@loshalcones.ejemplo", "+56 9 0000 0405", 6, "Tesorera"),
        Ejecutivos("18930571-0", "Sebastián Andrés", "Núñez Olivares", "snunez@itcordillera.ejemplo", "+56 9 0000 0406", 7, "Analista de finanzas"),
        Ejecutivos("12584396-4", "Mónica Patricia", "Leiva Castro", "mleiva@plazaoriente.ejemplo", "+56 9 0000 0407", 8, "Administradora de cobranza"),
    )

    private const val PEND = EstadoCompromisoId.PENDIENTE
    private const val VENC = EstadoCompromisoId.VENCIDO
    private const val PAG = EstadoCompromisoId.PAGADO
    private const val REV = EstadoCompromisoId.EN_REVISION
    private const val ANUL = EstadoCompromisoId.ANULADO

    private val COMPROMISOS_ANEXO = listOf(
        CompromisoSemilla("CP-2026-0064", 1, 8, "Arriendo departamento julio", 7, 12, 520000, "2026-07-10", PAG, 0, "2026-07-09T10:20:00", "2026-06-30T09:00:00", 7),
        CompromisoSemilla("CP-2026-0072", 2, 1, "Cuota pie departamento", 13, 36, 310000, "2026-08-31", PAG, 8500, "2026-09-02T21:14:00", "2026-08-21T09:00:00", 1),
        CompromisoSemilla("CP-2026-0081", 5, 2, "Matrícula 2026", null, null, 120000, "2026-09-01", ANUL, 0, "2026-09-05T11:00:00", "2026-08-22T09:00:00", 2),
        CompromisoSemilla("CP-2026-0089", 1, 8, "Arriendo departamento septiembre", 9, 12, 520000, "2026-09-10", PAG, 0, "2026-09-09T13:05:00", "2026-08-31T09:00:00", 7),
        CompromisoSemilla("CP-2026-0090", 1, 6, "Cuota social septiembre", 9, 12, 15000, "2026-09-05", PAG, 2000, "2026-09-07T18:40:00", "2026-08-26T09:00:00", 5),
        CompromisoSemilla("CP-2026-0091", 1, 5, "Aporte mensual septiembre", null, null, 10000, "2026-09-15", PAG, 0, "2026-09-15T08:22:00", "2026-09-01T09:00:00", null),
        CompromisoSemilla("CP-2026-0095", 8, 6, "Cuota de incorporación", null, null, 25000, "2026-10-10", REV, 0, "2026-09-30T00:00:00", "2026-09-28T09:00:00", 5),
        CompromisoSemilla("CP-2026-0098", 5, 2, "Mensualidad septiembre", 7, 10, 185000, "2026-09-10", PAG, 0, "2026-09-10T22:31:00", "2026-08-31T09:00:00", 2),
        CompromisoSemilla("CP-2026-0101", 1, 8, "Arriendo departamento octubre", 10, 12, 520000, "2026-10-10", PEND, 0, "2026-10-01T09:00:00", "2026-10-01T09:00:00", 7),
        CompromisoSemilla("CP-2026-0102", 1, 6, "Cuota social octubre", 10, 12, 15000, "2026-10-05", VENC, 2000, "2026-10-06T00:00:00", "2026-09-25T09:00:00", 5),
        CompromisoSemilla("CP-2026-0103", 1, 5, "Aporte mensual octubre", null, null, 10000, "2026-10-15", PEND, 0, "2026-10-01T09:00:00", "2026-10-01T09:00:00", null),
        CompromisoSemilla("CP-2026-0104", 1, 3, "Crédito de consumo", 7, 24, 86400, "2026-09-25", PAG, 0, "2026-09-24T10:42:00", "2026-09-15T09:00:00", 3),
        CompromisoSemilla("CP-2026-0105", 1, 3, "Crédito de consumo", 8, 24, 86400, "2026-10-25", PEND, 0, "2026-10-01T09:00:00", "2026-10-01T09:00:00", 3),
        CompromisoSemilla("CP-2026-0106", 2, 1, "Cuota pie departamento", 14, 36, 310000, "2026-09-30", VENC, 8500, "2026-10-01T00:00:00", "2026-09-20T09:00:00", 1),
        CompromisoSemilla("CP-2026-0107", 2, 1, "Cuota pie departamento", 15, 36, 310000, "2026-10-30", PEND, 0, "2026-10-01T09:00:00", "2026-10-01T09:00:00", 1),
        CompromisoSemilla("CP-2026-0108", 3, 4, "Seguro de hogar octubre", 10, 12, 18900, "2026-10-01", PAG, 0, "2026-09-30T19:05:00", "2026-09-21T09:00:00", null),
        CompromisoSemilla("CP-2026-0109", 4, 7, "Arancel octubre", 8, 10, 142000, "2026-10-05", REV, 0, "2026-10-03T00:00:00", "2026-09-25T09:00:00", 6),
        CompromisoSemilla("CP-2026-0110", 5, 2, "Mensualidad octubre", 8, 10, 185000, "2026-10-10", PEND, 0, "2026-10-01T09:00:00", "2026-10-01T09:00:00", 2),
        CompromisoSemilla("CP-2026-0111", 6, 3, "Crédito de consumo", 19, 36, 54200, "2026-09-20", ANUL, 0, "2026-09-22T09:30:00", "2026-09-10T09:00:00", 4),
        CompromisoSemilla("CP-2026-0112", 7, 6, "Cuota social octubre", 10, 12, 15000, "2026-10-05", PAG, 0, "2026-10-04T08:15:00", "2026-09-25T09:00:00", 5),
    )

    private val PAGOS_ANEXO = listOf(
        PagoSemilla("CPR-000377", "CP-2026-0072", "2026-09-02T21:14:00", 318500, 3, 3),
        PagoSemilla("CPR-000398", "CP-2026-0089", "2026-09-09T13:05:00", 520000, 2, 1),
        PagoSemilla("CPR-000402", "CP-2026-0090", "2026-09-07T18:40:00", 17000, 2, 1),
        PagoSemilla("CPR-000405", "CP-2026-0091", "2026-09-15T08:22:00", 10000, 3, 3),
        PagoSemilla("CPR-000447", "CP-2026-0098", "2026-09-10T22:31:00", 185000, 2, 2),
        PagoSemilla("CPR-000451", "CP-2026-0104", "2026-09-24T10:42:00", 86400, 2, 1),
        PagoSemilla("CPR-000452", "CP-2026-0108", "2026-09-30T19:05:00", 18900, 3, 3),
        PagoSemilla("CPR-000453", "CP-2026-0112", "2026-10-04T08:15:00", 15000, 1, 4),
        PagoSemilla("CPR-000300", "CP-2026-0064", "2026-07-09T10:20:00", 520000, 2, 1),
    )

    private data class RecomendacionSemilla(
        val numero: String, val usuarioFinal: Int, val organizacion: String,
        val rubro: String, val comuna: String, val fecha: String, val estado: Int,
    )

    private val RECOMENDACIONES = listOf(
        RecomendacionSemilla("REC-0001", 1, "Jardín Infantil Pequeños Exploradores", "Educación parvularia", "Maipú", "2026-08-12T10:00:00", EstadoRecomendacionId.SE_UNIO),
        RecomendacionSemilla("REC-0002", 1, "Comunidad Edificio Parque Los Olmos", "Administración de condominio", "Maipú", "2026-09-02T10:00:00", EstadoRecomendacionId.CONTACTADA),
        RecomendacionSemilla("REC-0003", 3, "Gimnasio Fuerza Sur", "Deporte", "San Miguel", "2026-09-15T10:00:00", EstadoRecomendacionId.EN_EVALUACION),
        RecomendacionSemilla("REC-0004", 5, "Academia de Música Clave de Sol", "Educación artística", "Ñuñoa", "2026-09-20T10:00:00", EstadoRecomendacionId.RECIBIDA),
        RecomendacionSemilla("REC-0005", 4, "Colegio Bosque Nativo", "Educación escolar", "La Florida", "2026-09-22T10:00:00", EstadoRecomendacionId.DESCARTADA),
        RecomendacionSemilla("REC-0006", 7, "Corporación Deportiva Cordillera", "Club deportivo", "San Bernardo", "2026-10-01T10:00:00", EstadoRecomendacionId.RECIBIDA),
    )

    private const val FECHA_REFERENCIA = "2026-10-06T00:00:00"

    fun cargar(db: SQLiteDatabase) {
        // Catalogos
        ROLES.forEachIndexed { i, (codigo, nombre, desc) ->
            db.insertar("rol", "id_rol" to i + 1, "codigo" to codigo, "nombre" to nombre, "descripcion" to desc)
        }

        catalogo(db, "comuna", "id_comuna", COMUNAS)
        catalogo(db, "area", "id_area", AREAS)
        catalogo(db, "rubro", "id_rubro", RUBROS)
        catalogo(db, "estado_compromiso", "id_estado_compromiso", ESTADOS_COMPROMISO)
        catalogo(db, "motivo_anulacion", "id_motivo_anulacion", MOTIVOS_ANULACION)
        catalogo(db, "canal", "id_canal", CANALES)
        catalogo(db, "medio_pago", "id_medio_pago", MEDIOS_PAGO)
        catalogo(db, "motivo_reporte", "id_motivo_reporte", MOTIVOS_REPORTE)
        catalogo(db, "estado_reporte", "id_estado_reporte", ESTADOS_REPORTE)

        ESTADOS_RECOMENDACION.forEachIndexed { i, (nombre, orden) ->
            db.insertar("estado_recomendacion", "id_estado_recomendacion" to i + 1, "nombre" to nombre, "orden" to orden)
        }

        RECONOCIMIENTOS.forEachIndexed { i, (nombre, desc) ->
            db.insertar("reconocimiento", "id_reconocimiento" to i + 1, "nombre" to nombre, "descripcion" to desc)
        }

        // Empresas
        EMPRESAS.forEachIndexed { i, e ->
            db.insertar(
                "empresa_cliente",
                "id_empresa" to i + 1, "codigo" to e.codigo, "razon_social" to e.razonSocial,
                "nombre_fantasia" to e.nombreFantasia, "rut" to e.rut, "id_rubro" to RUBROS.indexOf(e.rubro) + 1,
                "correo_atencion" to e.correo, "telefono_atencion" to e.telefono,
                "recargo_atraso" to e.recargo, "activa" to true,
            )
        }

        // Usuarios
        val hash = Seguridad.hash(CONTRASENA_DEMO)
        val idRolFinal = 1
        val idRolAgente = 2
        val idRolEjecutivo = 3

        USUARIOS_FINALES.forEach { u ->
            db.insertar(
                "usuario",
                "id_rol" to idRolFinal, "rut" to u.rut, "nombres" to u.nombres, "apellidos" to u.apellidos,
                "correo" to u.correo, "contrasena_hash" to hash, "telefono" to u.telefono, "activo" to true,
                "fecha_creacion" to FECHA_REFERENCIA,
                "id_comuna" to COMUNAS.indexOf(u.comuna) + 1,
                "fecha_incorporacion" to u.incorporacion,
                "avisos_activos" to (u.diasAviso != null),
                "dias_anticipacion" to u.diasAviso,
            )
        }

        AGENTES.forEach { a ->
            db.insertar(
                "usuario",
                "id_rol" to idRolAgente, "rut" to a.rut, "nombres" to a.nombres, "apellidos" to a.apellidos,
                "correo" to a.correo, "contrasena_hash" to hash, "activo" to a.activo,
                "fecha_creacion" to FECHA_REFERENCIA,
                "id_area" to AREAS.indexOf(a.area) + 1,
            )
        }

        EJECUTIVOS.forEach { e ->
            db.insertar(
                "usuario",
                "id_rol" to idRolEjecutivo, "rut" to e.rut, "nombres" to e.nombres, "apellidos" to e.apellidos,
                "correo" to e.correo, "contrasena_hash" to hash, "telefono" to e.telefono, "activo" to true,
                "fecha_creacion" to FECHA_REFERENCIA,
                "id_empresa" to e.idEmpresa, "cargo" to e.cargo,
            )
        }

        val primerIdAgente = USUARIOS_FINALES.size + 1
        val primerIdEjecutivo = USUARIOS_FINALES.size + AGENTES.size + 1

        val idCompromiso = HashMap<String, Long>()
        (COMPROMISOS_ANEXO + DatosHistoricos.COMPROMISOS).forEach { c ->
            idCompromiso[c.folio] = db.insertar(
                "compromiso",
                "folio" to c.folio, "id_usuario_final" to c.usuarioFinal, "id_empresa" to c.empresa,
                "concepto" to c.concepto, "numero_cuota" to c.cuota, "total_cuotas" to c.totalCuotas,
                "monto" to c.monto, "fecha_vencimiento" to c.vencimiento, "id_estado_compromiso" to c.estado,
                "recargo_aplicado" to c.recargo, "fecha_cambio_estado" to c.cambioEstado,
                "id_ejecutivo_registro" to c.ejecutivo?.let { primerIdEjecutivo + it - 1 },
                "fecha_registro" to c.registro,
            )
        }
        (PAGOS_ANEXO + DatosHistoricos.PAGOS).forEach { p ->
            db.insertar(
                "pago",
                "numero_comprobante" to p.comprobante, "id_compromiso" to idCompromiso.getValue(p.folio),
                "fecha_hora" to p.fechaHora, "monto_pagado" to p.monto, "id_canal" to p.canal, "id_medio_pago" to p.medioPago,
            )
        }

        db.insertar(
            "anulacion", "id_compromiso" to idCompromiso.getValue("CP-2026-0111"), "id_ejecutivo" to primerIdEjecutivo + 3,
            "id_motivo_anulacion" to 1, "comentario" to null, "fecha" to "2026-09-22T09:30:00",
        )
        db.insertar(
            "anulacion", "id_compromiso" to idCompromiso.getValue("CP-2026-0081"), "id_ejecutivo" to primerIdEjecutivo + 1,
            "id_motivo_anulacion" to 2, "comentario" to "Cobro duplicado", "fecha" to "2026-09-05T11:00:00",
        )

        fun reporte(
            numero: String, folio: String, motivo: Int, comentario: String, creacion: String, estado: Int,
            agente: Int?, toma: String?, respuesta: String?, resolucion: String?,
        ) = db.insertar(
            "reporte",
            "numero" to numero, "id_compromiso" to idCompromiso.getValue(folio), "id_motivo_reporte" to motivo,
            "comentario" to comentario, "fecha_creacion" to creacion, "id_estado_reporte" to estado,
            "id_agente" to agente?.let { primerIdAgente + it - 1 }, "fecha_toma" to toma,
            "respuesta" to respuesta, "fecha_resolucion" to resolucion,
        )
        reporte("REP-0005", "CP-2026-0064", 4, "El concepto dice \"agosto\" y es la cuota de julio.", "2026-07-28T10:00:00",
            3, 1, "2026-07-29T09:00:00", "Se corrigió el concepto; el monto era correcto.", "2026-07-30T12:00:00")
        reporte("REP-0007", "CP-2026-0081", 2, "Me aparece una matrícula que ya pagué en marzo.", "2026-09-02T09:30:00",
            3, 2, "2026-09-03T09:00:00", "Cobro duplicado anulado por el colegio.", "2026-09-05T11:00:00")
        reporte("REP-0009", "CP-2026-0111", 3, "Repacté el crédito y me siguen cobrando la cuota antigua.", "2026-09-18T18:10:00",
            3, 1, "2026-09-19T09:00:00", "La empresa anuló la cuota por repactación.", "2026-09-22T09:30:00")
        // REP-0010: Abierto y con más de 2 días hábiles sin gestionar (atrasado, RN-23)
        reporte("REP-0010", "CP-2026-0095", 2, "Me llegó un cobro de un club al que no pertenezco.", "2026-09-30T00:00:00",
            1, null, null, null, null)
        reporte("REP-0011", "CP-2026-0109", 1, "Pagué por transferencia directa al instituto el 03-10.", "2026-10-03T00:00:00",
            2, 2, "2026-10-05T10:00:00", null, null)

        RECOMENDACIONES.forEachIndexed { i, r ->
            db.insertar(
                "recomendacion",
                "id_recomendacion" to i + 1, "numero" to r.numero, "id_usuario_final" to r.usuarioFinal,
                "nombre_organizacion" to r.organizacion, "id_rubro" to RUBROS.indexOf(r.rubro) + 1,
                "id_comuna" to COMUNAS.indexOf(r.comuna) + 1, "fecha" to r.fecha, "id_estado_recomendacion" to r.estado,
            )
        }
        // Contactos autorizados (RN-12): solo existen donde el anexo indica "Autoriza contacto: Sí"
        db.insertar("contacto_recomendacion", "id_recomendacion" to 1, "nombre" to "Paula Herrera",
            "correo" to "pherrera@pexploradores.ejemplo", "telefono" to null, "autoriza_contacto" to true)
        db.insertar("contacto_recomendacion", "id_recomendacion" to 3, "nombre" to "Raúl Ibáñez",
            "correo" to null, "telefono" to "+56 9 0000 0301", "autoriza_contacto" to true)
        db.insertar("contacto_recomendacion", "id_recomendacion" to 6, "nombre" to "Ana Valdés",
            "correo" to "avaldes@cdcordillera.ejemplo", "telefono" to null, "autoriza_contacto" to true)
        fun cambioEstado(idRecomendacion: Int, estado: Int, agente: Int, fecha: String, motivo: String? = null) = db.insertar(
            "historial_recomendacion",
            "id_recomendacion" to idRecomendacion, "id_estado_recomendacion" to estado,
            "id_agente" to primerIdAgente + agente - 1, "fecha" to fecha, "motivo" to motivo,
        )
        cambioEstado(1, EstadoRecomendacionId.SE_UNIO, 4, "2026-09-25T10:00:00")
        cambioEstado(2, EstadoRecomendacionId.CONTACTADA, 5, "2026-09-10T10:00:00")
        cambioEstado(3, EstadoRecomendacionId.EN_EVALUACION, 4, "2026-09-28T10:00:00")
        cambioEstado(5, EstadoRecomendacionId.DESCARTADA, 5, "2026-09-23T10:00:00", "La organización ya es cliente de PagoFlex")
    }

    private fun catalogo(db: SQLiteDatabase, tabla: String, columnaId: String, nombres: List<String>) {
        nombres.forEachIndexed { i, nombre -> db.insertar(tabla, columnaId to i + 1, "nombre" to nombre) }
    }

    private fun SQLiteDatabase.insertar(tabla: String, vararg campos: Pair<String, Any?>): Long {
        val valores = ContentValues()
        for ((columna, valor) in campos) {
            when (valor) {
                null -> valores.putNull(columna)
                is Int -> valores.put(columna, valor)
                is Long -> valores.put(columna, valor)
                is Boolean -> valores.put(columna, if (valor) 1 else 0)
                is String -> valores.put(columna, valor)
                else -> error("Tipo no soportado en la semilla: ${valor::class}")
            }
        }
        return insertOrThrow(tabla, null, valores)
    }
}