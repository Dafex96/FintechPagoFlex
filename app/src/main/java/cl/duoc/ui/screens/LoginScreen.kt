package cl.duoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.data.DatosIniciales
import cl.duoc.model.CuentaDemo
import cl.duoc.model.ErrorLogin
import cl.duoc.model.LoginUiState
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.BotonSecundario
import cl.duoc.ui.components.CampoContrasena
import cl.duoc.ui.components.CampoTexto
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    onIrARegistro: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        onRutCambia = viewModel::onRutCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onIngresar = { viewModel.onIngresar()?.let { usuario -> onLoginExitoso(usuario) } },
        onUsarCuentaDemo = viewModel::onUsarCuentaDemo,
        onIrARegistro = onIrARegistro
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onRutCambia: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onIngresar: () -> Unit,
    onUsarCuentaDemo: (CuentaDemo) -> Unit,
    onIrARegistro: () -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = Dimens.anchoMaximoContenido)
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.espacioPantalla, vertical = Dimens.espacioExtraGrande),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)) {
                    Text(
                        text = stringResource(R.string.login_titulo),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = stringResource(R.string.login_subtitulo),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                CampoTexto(
                    valor = uiState.rut,
                    onValorCambia = onRutCambia,
                    etiqueta = stringResource(R.string.login_campo_rut),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorRut?.texto(),
                    ayuda = stringResource(R.string.login_rut_ayuda),
                    capitalizacion = KeyboardCapitalization.Characters
                )
                CampoContrasena(
                    valor = uiState.contrasena,
                    onValorCambia = onContrasenaCambia,
                    etiqueta = stringResource(R.string.login_campo_contrasena),
                    visible = uiState.contrasenaVisible,
                    onAlternarVisible = onAlternarVisibilidad,
                    error = uiState.errorContrasena?.texto(),
                    onListo = onIngresar
                )

                uiState.errorGeneral?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { liveRegion = LiveRegionMode.Polite },
                        shape = EstilosTarjeta.forma,
                        colors = EstilosTarjeta.coloresError()
                    ) {
                        Text(
                            text = error.texto(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(Dimens.espacioMedio)
                        )
                    }
                }

                BotonPrincipal(
                    texto = stringResource(R.string.login_btn_ingresar),
                    onClick = onIngresar,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.login_sin_cuenta),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onIrARegistro) {
                        Text(text = stringResource(R.string.login_btn_crear_cuenta))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.espacioChico))

                Text(
                    text = stringResource(R.string.login_demo_titulo),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.login_demo_ayuda, DatosIniciales.CONTRASENA_DEMO),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                CuentaDemo.TODAS.forEach { cuenta ->
                    BotonSecundario(
                        texto = stringResource(
                            R.string.login_demo_cuenta, cuenta.nombre, nombreDeRol(cuenta.rolCodigo)
                        ),
                        onClick = { onUsarCuentaDemo(cuenta) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = stringResource(R.string.aviso_datos_ficticios),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.espacioChico)
                )
            }
        }
    }
}

@Composable
private fun ErrorLogin.texto(): String = stringResource(
    when (this) {
        ErrorLogin.CAMPO_VACIO -> R.string.error_campo_obligatorio
        ErrorLogin.RUT_INVALIDO -> R.string.error_rut_invalido
        ErrorLogin.CREDENCIALES -> R.string.login_error_credenciales
        ErrorLogin.INACTIVO -> R.string.login_error_inactivo
        ErrorLogin.GENERICO -> R.string.login_error_generico
    }
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginVacioPreview() {
    PagoFlexTheme {
        LoginContent(
            uiState = LoginUiState(),
            onRutCambia = {}, onContrasenaCambia = {}, onAlternarVisibilidad = {},
            onIngresar = {}, onUsarCuentaDemo = {}, onIrARegistro = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1100, name = "Con errores")
@Composable
private fun LoginConErroresPreview() {
    PagoFlexTheme {
        LoginContent(
            uiState = LoginUiState(
                rut = "15834207-4",
                errorRut = ErrorLogin.RUT_INVALIDO,
                errorContrasena = ErrorLogin.CAMPO_VACIO,
                errorGeneral = ErrorLogin.CREDENCIALES
            ),
            onRutCambia = {}, onContrasenaCambia = {}, onAlternarVisibilidad = {},
            onIngresar = {}, onUsarCuentaDemo = {}, onIrARegistro = {}
        )
    }
}