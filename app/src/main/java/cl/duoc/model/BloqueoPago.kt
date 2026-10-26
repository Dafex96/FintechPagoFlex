package cl.duoc.model

sealed class BloqueoPago {
    data object Pagado : BloqueoPago()
    data object Anulado : BloqueoPago()
    data object EnRevision : BloqueoPago()
    data class PrimeroPagar(val previo: Compromiso) : BloqueoPago()
}