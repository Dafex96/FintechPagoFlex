package cl.duoc.notificaciones

import cl.duoc.model.Compromiso

/**
 * Reglas de RN-15 en un solo lugar y sin dependencias de Android, para poder probarlas con tests unitarios.
 */
object PoliticaAvisos {

    const val HORA_INICIO_SILENCIO = 22
    const val HORA_FIN_SILENCIO = 8

    enum class TipoAviso { PREVIO, HOY, VENCIDO }

    /** Nunca se avisa entre las 22:00 y las 08:00. */
    fun enHorarioSilencioso(hora: Int): Boolean =
        hora >= HORA_INICIO_SILENCIO || hora < HORA_FIN_SILENCIO

    /**
     * Qué aviso corresponde a un compromiso, o null si no corresponde ninguno.
     * Solo los Pendientes y Vencidos pueden generar avisos: Pagado, Anulado y En revisión quedan fuera.
     */
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
