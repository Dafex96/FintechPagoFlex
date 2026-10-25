package cl.duoc.model

data class Pago(
    val id: Long,
    val numeroComprobante: String,
    val idCompromiso: Long,
    val fechaHora: String,
    val montoPagado: Int,
    val idCanal: Long,
    val canalNombre: String,
    val idMedioPago: Long,
    val medioPagoNombre: String,
    val empresaNombre: String = "",
    val concepto: String = "",
    val folioCompromiso: String = ""
)
