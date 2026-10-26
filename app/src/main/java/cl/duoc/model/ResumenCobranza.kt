package cl.duoc.model

data class ResumenCobranza(
    val empresaNombre: String = "",
    val mes: String = "",
    val montoRecaudado: Int = 0,
    val montoPendiente: Int = 0,
    val montoVencido: Int = 0,
    val totalCompromisos: Int = 0,
    val pagadosCount: Int = 0,
    val pendientesCount: Int = 0,
    val vencidosCount: Int = 0,
    val enRevisionCount: Int = 0,
    val anuladosCount: Int = 0
) {
    val baseCobranza: Int get() = totalCompromisos - anuladosCount

    val tasaCobranza: Float
        get() = if (baseCobranza > 0) pagadosCount * 100f / baseCobranza else 0f
}