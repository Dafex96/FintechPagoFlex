package cl.duoc.model

data class PreferenciasAvisos(
    val activos: Boolean,
    val diasAnticipacion: Int = AvisoAnticipacion.POR_DEFECTO
)
