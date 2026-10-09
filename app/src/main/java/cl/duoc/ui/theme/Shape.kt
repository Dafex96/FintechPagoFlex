package cl.duoc.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Formas
val PagoFlexShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

// Espaciados
object Spacing {
    val xs: Dp = 4.dp
    val s: Dp = 8.dp
    val m: Dp = 16.dp
    val l: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
}

// Dimens dentro del Shape
object Dimens {

    val AreaTactilMinima: Dp = 48.dp

    val BotonAlto: Dp = 56.dp

    val MargenPantalla: Dp = 20.dp

    val AnchoMaximoContenido: Dp = 600.dp

    val IconoEstado: Dp = 20.dp
    val IconoNormal: Dp = 24.dp
    val IconoGrande: Dp = 48.dp
    val Avatar: Dp = 72.dp
}