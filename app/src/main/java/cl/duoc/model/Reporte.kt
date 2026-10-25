package cl.duoc.model

data class Reporte(
    val id: Long,
    val numero: String,
    val idCompromiso: Long,
    val folioCompromiso: String = "",
    val empresaNombre: String = "",
    val concepto: String = "",
    val idMotivoReporte: Long,
    val motivoNombre: String = "",
    val comentario: String? = null,
    val fechaCreacion: String,
    val idEstadoReporte: Long,
    val estadoNombre: String = "",
    val idAgente: Long? = null,
    val agenteNombre: String? = null,
    val fechaToma: String? = null,
    val respuesta: String? = null,
    val fechaResolucion: String? = null
)
