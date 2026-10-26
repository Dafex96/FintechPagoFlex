package cl.duoc.model

data class ResumenInicioUsuario(
    val totalMes: Int = 0,
    val proximoCompromiso: Compromiso? = null,
    val cantidadVencidos: Int = 0,
    val compromisosPendientes: List<Compromiso> = emptyList(),
    val compromisosVencidos: List<Compromiso> = emptyList(),
    val cumplimiento: IndicadorCumplimiento = IndicadorCumplimiento()
)