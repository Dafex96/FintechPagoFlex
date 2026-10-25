package cl.duoc.model

data class Reconocimiento(
    val idReconocimiento: Int,
    val nombre: String,
    val descripcion: String
)

/** Fila de usuario_reconocimiento: un reconocimiento ya obtenido por la persona. */
data class UsuarioReconocimiento(
    val idUsuarioFinal: Long,
    val idReconocimiento: Int,
    val fechaObtencion: String,
    val motivo: String
)

object ReconocimientoId {
    const val PRIMER_PAGO = 1
    const val PERSONA_PUNTUAL = 2
    const val TRES_MESES = 3
    const val SEIS_MESES = 4
    const val DOCE_MESES = 5
    const val IMPULSOR = 6
}

enum class TipoProgreso { NINGUNO, PAGOS, MESES }

/** Lo que la pantalla necesita para mostrar un reconocimiento, obtenido o por obtener. */
data class ReconocimientoItem(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val obtenido: Boolean,
    val fechaObtencion: String? = null,
    val motivo: String? = null,
    val tipoProgreso: TipoProgreso = TipoProgreso.NINGUNO,
    val progresoActual: Int = 0,
    val progresoMeta: Int = 0
) {
    val fraccionProgreso: Float
        get() = if (progresoMeta > 0) (progresoActual.toFloat() / progresoMeta).coerceIn(0f, 1f) else 0f
}
