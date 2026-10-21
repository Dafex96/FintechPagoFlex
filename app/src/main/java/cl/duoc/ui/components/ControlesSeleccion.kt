package cl.duoc.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme

@Composable
fun CasillaConTexto(
    texto: String,
    marcado: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(value = marcado, onValueChange = onCambio, role = Role.Checkbox)
                .padding(vertical = Dimens.espacioMini),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = marcado, onCheckedChange = null)
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = Dimens.espacioChico)
            )
        }
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Dimens.espacioMedio)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlesSeleccionPreview() {
    PagoFlexTheme {
        Column(Modifier.padding(16.dp)) {
            CasillaConTexto(texto = "Acepto los términos y la política de datos", marcado = true, onCambio = {})
            CasillaConTexto(
                texto = "Acepto los términos y la política de datos",
                marcado = false,
                onCambio = {},
                error = "Debes aceptar los términos para continuar"
            )
        }
    }
}