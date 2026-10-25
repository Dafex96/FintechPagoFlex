package cl.duoc.model

data class Compromiso(
    val id: Long,
    val folio: String,
    val idUsuarioFinal: Long,
    val idEmpresa: Long,
    val empresaNombre: String,
    val empresaTelefono: String = "",
    val empresaCorreo: String = "",
    val concepto: String,
    val numeroCuota: Int? = null,
    val totalCuotas: Int? = null,
    val monto: Int,
    val fechaVencimiento: String,
    val idEstadoCompromiso: Long,
    val estadoNombre: String,
    val recargoAplicado: Int = 0,
    val fechaCambioEstado: String = "",
    val fechaRegistro: String = ""
) {
    val montoTotal: Int
        get() = monto + recargoAplicado

    val esPendiente: Boolean
        get() = idEstadoCompromiso == EstadoCompromisoId.PENDIENTE.toLong()

    val esVencido: Boolean
        get() = idEstadoCompromiso == EstadoCompromisoId.VENCIDO.toLong()

    val esPagado: Boolean
        get() = idEstadoCompromiso == EstadoCompromisoId.PAGADO.toLong()

    val esEnRevision: Boolean
        get() = idEstadoCompromiso == EstadoCompromisoId.EN_REVISION.toLong()

    val esAnulado: Boolean
        get() = idEstadoCompromiso == EstadoCompromisoId.ANULADO.toLong()

    val puedePagar: Boolean
        get() = esPendiente || esVencido

    val textoCuota: String?
        get() = if (numeroCuota != null && totalCuotas != null) "Cuota $numeroCuota/$totalCuotas" else null
}
