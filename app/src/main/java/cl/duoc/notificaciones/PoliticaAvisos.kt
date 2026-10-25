package cl.duoc.notificaciones

import cl.duoc.model.Compromiso

object PoliticaAvisos {

    const val HORA_INICIO_SILENCIO = 22
    const val HORA_FIN_SILENCIO = 8

    enum class TipoAviso { PREVIO, HOY, VENCIDO }

    fun enHorarioSilencioso(hora: Int): Boolean =
        hora >= HORA_INICIO_SILENCIO || hora < HORA_FIN_SILENCIO

    fun tipoDeAviso(
        compromiso: Compromiso,
        diasAnticipacion: Int,
        diasParaVencer: Int
    ): TipoAviso? = when {
        compromiso.esPendiente && diasParaVencer == 0 -> TipoAviso.HOY
        compromiso.esPendiente && diasParaVencer in 1..diasAnticipacion -> TipoAviso.PREVIO
        compromiso.esVencido -> TipoAviso.VENCIDO
        else -> null
    }
}
