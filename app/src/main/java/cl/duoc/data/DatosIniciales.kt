package cl.duoc.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
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
        "Educación superior", "Administración de arriendos", "Educación parvularia", "Otro",
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
    }

    // ----- Ayudas privadas -----

    private fun catalogo(db: SQLiteDatabase, tabla: String, columnaId: String, nombres: List<String>) {
        nombres.forEachIndexed { i, nombre -> db.insertar(tabla, columnaId to i + 1, "nombre" to nombre) }
    }

    /** Inserta una fila. ContentValues escapa los textos por nosotros (sin riesgo de inyección SQL). */
    private fun SQLiteDatabase.insertar(tabla: String, vararg campos: Pair<String, Any?>) {
        val valores = ContentValues()
        for ((columna, valor) in campos) {
            when (valor) {
                null -> valores.putNull(columna)
                is Int -> valores.put(columna, valor)
                is Boolean -> valores.put(columna, if (valor) 1 else 0)
                is String -> valores.put(columna, valor)
                else -> error("Tipo no soportado en la semilla: ${valor::class}")
            }
        }
        insertOrThrow(tabla, null, valores)
    }
}