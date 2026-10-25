package cl.duoc.model

data class ResumenCobranza(
    val empresaNombre: String = "",
    val montoRecaudado: Int = 0,
    val montoPendiente: Int = 0,
    val montoVencido: Int = 0,
    val totalCompromisos: Int = 0,
    val pagadosCount: Int = 0,
    val pendientesCount: Int = 0,
    val vencidosCount: Int = 0,
    val anuladosCount: Int = 0
) {
    val tasaCobranza: Float
        get() = if (totalCompromisos > 0) (pagadosCount.toFloat() / totalCompromisos.toFloat()) * 100f else 0f
}
