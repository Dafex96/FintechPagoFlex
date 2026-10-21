package cl.duoc.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import cl.duoc.ui.theme.Dimens

object EstilosBoton {

    val forma: Shape = RoundedCornerShape(Dimens.radioBoton)
    val relleno = PaddingValues(horizontal = 24.dp, vertical = 12.dp)

    @Composable
    fun coloresPrincipal(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    @Composable
    fun coloresSecundario(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    )

    @Composable
    fun bordeSecundario(): BorderStroke =
        BorderStroke(Dimens.bordeBoton, MaterialTheme.colorScheme.primary)

    @Composable
    fun elevacion(): ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = Dimens.elevacionBoton,
        pressedElevation = Dimens.elevacionBoton
    )
}

fun Modifier.estiloAltoBoton(): Modifier = this.heightIn(min = Dimens.alturaBoton)