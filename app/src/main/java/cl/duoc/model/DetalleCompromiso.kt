package cl.duoc.model

data class DetalleCompromiso(
    val compromiso: Compromiso,
    val pago: Pago? = null,
    val reporte: Reporte? = null,
    val diasAtraso: Int = 0
)