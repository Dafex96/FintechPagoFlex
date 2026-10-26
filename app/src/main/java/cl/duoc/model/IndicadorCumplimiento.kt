package cl.duoc.model

enum class CategoriaCumplimiento { EJEMPLAR, AL_DIA, EN_CAMINO }

data class IndicadorCumplimiento(
    val considerados: Int = 0,
    val aTiempo: Int = 0
) {
    val suficiente: Boolean get() = considerados >= MINIMO_COMPROMISOS

    val porcentaje: Double
        get() = if (considerados == 0) 0.0 else Math.round(aTiempo * 1000.0 / considerados) / 10.0

    val categoria: CategoriaCumplimiento?
        get() = when {
            !suficiente -> null
            porcentaje >= 95.0 -> CategoriaCumplimiento.EJEMPLAR
            porcentaje >= 80.0 -> CategoriaCumplimiento.AL_DIA
            else -> CategoriaCumplimiento.EN_CAMINO
        }

    companion object {
        const val MINIMO_COMPROMISOS = 3
    }
}