package cl.duoc.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import cl.duoc.R
import cl.duoc.ui.styles.EstilosCampos
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme

@Composable
fun CampoContrasena(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    visible: Boolean,
    onAlternarVisible: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    ayuda: String? = null,
    accionTeclado: ImeAction = ImeAction.Done,
    onListo: () -> Unit = {}
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
                painter = painterResource(R.drawable.ic_candado),
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        trailingIcon = {
            IconButton(onClick = onAlternarVisible) {
                Icon(
                    painter = painterResource(
                        if (visible) R.drawable.ic_ojo_tachado else R.drawable.ic_ojo
                    ),
                    contentDescription = stringResource(
                        if (visible) R.string.cd_ocultar_contrasena else R.string.cd_mostrar_contrasena
                    ),
                    modifier = Modifier.size(Dimens.tamanoIcono)
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = error != null,
        supportingText = {
            val texto = error ?: ayuda
            if (texto != null) Text(texto)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = accionTeclado
        ),
        keyboardActions = KeyboardActions(onDone = { onListo() }),
        shape = EstilosCampos.forma,
        colors = EstilosCampos.colores()
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoContrasenaPreview() {
    PagoFlexTheme {
        CampoContrasena(
            valor = "secreto",
            onValorCambia = {},
            etiqueta = "Contraseña",
            visible = false,
            onAlternarVisible = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}