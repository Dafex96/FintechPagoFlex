package cl.duoc.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme

@Composable
fun AvatarCircular(
    iniciales: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    foto: ImageBitmap? = null
) {
    val medidas = modifier
        .size(Dimens.tamanoAvatar)
        .clip(CircleShape)

    if (foto != null) {
        Image(
            painter = BitmapPainter(foto),
            contentDescription = descripcion,
            contentScale = ContentScale.Crop,
            modifier = medidas.border(Dimens.bordeAvatar, MaterialTheme.colorScheme.surface, CircleShape)
        )
    } else {
        Box(
            modifier = medidas
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(Dimens.bordeAvatar, MaterialTheme.colorScheme.surface, CircleShape)
                .clearAndSetSemantics { contentDescription = descripcion },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarCircularSinFotoPreview() {
    PagoFlexTheme {
        AvatarCircular(
            iniciales = "CR",
            descripcion = "Foto de perfil de Camila Rojas",
            modifier = Modifier.padding(16.dp)
        )
    }
}