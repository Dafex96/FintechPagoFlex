package cl.duoc.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.R
import cl.duoc.ui.styles.EstilosCampos
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme

@Composable
fun CampoTexto(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    @DrawableRes icono: Int,
    modifier: Modifier = Modifier,
    error: String? = null,
    ayuda: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizacion: KeyboardCapitalization = KeyboardCapitalization.None,
    accionTeclado: ImeAction = ImeAction.Next
) {

    val mensajeError = error

    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier
            .fillMaxWidth()
            .semantics { if (mensajeError != null) error(mensajeError) },
        label = { Text(etiqueta) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        isError = error != null,
        supportingText = {
            val texto = error ?: ayuda
            if (texto != null) Text(texto)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = capitalizacion,
            keyboardType = tipoTeclado,
            imeAction = accionTeclado
        ),
        shape = EstilosCampos.forma,
        colors = EstilosCampos.colores()
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoTextoPreview() {
    PagoFlexTheme {
        CampoTexto(
            valor = "15834207-4",
            onValorCambia = {},
            etiqueta = "RUT",
            icono = R.drawable.ic_persona,
            error = "Revisa el RUT: parece que no es válido",
            modifier = Modifier.padding(16.dp)
        )
    }
}