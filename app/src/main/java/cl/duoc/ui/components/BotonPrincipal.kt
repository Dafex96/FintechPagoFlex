package cl.duoc.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.R
import cl.duoc.ui.styles.EstilosBoton
import cl.duoc.ui.styles.estiloAltoBoton
import cl.duoc.ui.theme.PagoFlexTheme

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    @DrawableRes icono: Int? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.estiloAltoBoton(),
        enabled = habilitado,
        shape = EstilosBoton.forma,
        colors = EstilosBoton.coloresPrincipal(),
        elevation = EstilosBoton.elevacion(),
        contentPadding = EstilosBoton.relleno
    ) {
        if (icono != null) {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(text = texto)
    }
}

@Preview(showBackground = true)
@Composable
private fun BotonPrincipalPreview() {
    PagoFlexTheme {
        Column(Modifier.padding(16.dp)) {
            BotonPrincipal(texto = "Ingresar", onClick = {}, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.size(8.dp))
            BotonPrincipal(texto = "Con ícono", onClick = {}, icono = R.drawable.ic_check, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.size(8.dp))
            BotonPrincipal(texto = "Deshabilitado", onClick = {}, habilitado = false, modifier = Modifier.fillMaxWidth())
        }
    }
}