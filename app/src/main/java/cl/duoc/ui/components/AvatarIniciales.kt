package cl.duoc.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme

@Composable
fun AvatarIniciales(
    iniciales: String,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(Dimens.tamanoAvatar)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
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

@Preview(showBackground = true)
@Composable
private fun AvatarInicialesPreview() {
    PagoFlexTheme {
        AvatarIniciales(iniciales = "CR", descripcion = "Avatar de Camila Rojas", modifier = Modifier.padding(16.dp))
    }
}