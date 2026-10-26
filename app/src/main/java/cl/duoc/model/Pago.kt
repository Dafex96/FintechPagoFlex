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
    val folioCompromiso: String = "",
    val fechaVencimiento: String = "",
    val numeroCuota: Int? = null,
    val totalCuotas: Int? = null
) {
    val aTiempo: Boolean
        get() = fechaVencimiento.isNotEmpty() && fechaHora.take(10) <= fechaVencimiento.take(10)

    val textoCuota: String?
        get() = if (numeroCuota != null && totalCuotas != null) "Cuota $numeroCuota/$totalCuotas" else null
}