package cl.duoc.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cl.duoc.ui.theme.Blanco
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.GrisDeshabilitado
import cl.duoc.ui.theme.Primario

object EstilosBoton {

    val forma = RoundedCornerShape(Dimens.radioBoton)
    val relleno = PaddingValues(horizontal = 20.dp, vertical = 12.dp)

    @Composable
    fun coloresPrincipales(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Primario,
        contentColor = Blanco,
        disabledContainerColor = GrisDeshabilitado,
        disabledContentColor = Blanco
    )

    @Composable
    fun coloresSecundarios(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    )

    @Composable
    fun bordeSecundario(): BorderStroke = BorderStroke(Dimens.bordeBoton, MaterialTheme.colorScheme.secondary)

    @Composable
    fun elevacion(): ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = Dimens.elevacionBoton,
        pressedElevation = 1.dp
    )

}