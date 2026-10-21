package cl.duoc.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import cl.duoc.ui.theme.Dimens

object EstilosTarjeta {

    val forma: Shape = RoundedCornerShape(Dimens.radioTarjeta)

    @Composable
    fun colores(): CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )

    @Composable
    fun coloresDestacada(): CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )

    @Composable
    fun elevacion(): CardElevation = CardDefaults.cardElevation(
        defaultElevation = Dimens.elevacionTarjeta
    )

    @Composable
    fun borde(): BorderStroke =
        BorderStroke(Dimens.bordeTarjeta, MaterialTheme.colorScheme.outlineVariant)
}