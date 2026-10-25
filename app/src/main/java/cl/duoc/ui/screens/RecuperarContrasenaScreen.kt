package cl.duoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.ErrorRecuperacion
import cl.duoc.model.PasoRecuperacion
import cl.duoc.model.RecuperarContrasenaUiState
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.CampoContrasena
import cl.duoc.ui.components.CampoTexto
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.viewmodel.RecuperarContrasenaViewModel

@Composable
fun RecuperarContrasenaScreen(
    onVolverALogin: () -> Unit,
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecuperarContrasenaContent(
        uiState = uiState,
        onIdentificadorCambia = viewModel::onIdentificadorCambia,
        onCodigoCambia = viewModel::onCodigoCambia,
        onNuevaContrasenaCambia = viewModel::onNuevaContrasenaCambia,
        onConfirmarContrasenaCambia = viewModel::onConfirmarContrasenaCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onSolicitarCodigo = viewModel::onSolicitarCodigo,
        onVerificarCodigo = viewModel::onVerificarCodigo,
        onRestablecerContrasena = { viewModel.onRestablecerContrasena() },
        onVolverAPasoAnterior = viewModel::onVolverAPasoAnterior,
        onVolverALogin = onVolverALogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarContrasenaContent(
    uiState: RecuperarContrasenaUiState,
    onIdentificadorCambia: (String) -> Unit,
    onCodigoCambia: (String) -> Unit,
    onNuevaContrasenaCambia: (String) -> Unit,
    onConfirmarContrasenaCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onSolicitarCodigo: () -> Unit,
    onVerificarCodigo: () -> Unit,
    onRestablecerContrasena: () -> Unit,
    onVolverAPasoAnterior: () -> Unit,
    onVolverALogin: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.recuperar_titulo)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.paso == PasoRecuperacion.SOLICITUD_IDENTIFICADOR || uiState.paso == PasoRecuperacion.EXITO) {
                                onVolverALogin()
                            } else {
                                onVolverAPasoAnterior()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_volver),
                            contentDescription = stringResource(R.string.cd_volver)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
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
                    .padding(horizontal = Dimens.espacioPantalla, vertical = Dimens.espacioMedio),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioGrande)
            ) {
                when (uiState.paso) {
                    PasoRecuperacion.SOLICITUD_IDENTIFICADOR -> PasoSolicitud(
                        uiState = uiState,
                        onIdentificadorCambia = onIdentificadorCambia,
                        onSolicitarCodigo = onSolicitarCodigo
                    )

                    PasoRecuperacion.VERIFICACION_CODIGO -> PasoVerificacion(
                        uiState = uiState,
                        onCodigoCambia = onCodigoCambia,
                        onVerificarCodigo = onVerificarCodigo
                    )

                    PasoRecuperacion.NUEVA_CONTRASENA -> PasoNuevaContrasena(
                        uiState = uiState,
                        onNuevaContrasenaCambia = onNuevaContrasenaCambia,
                        onConfirmarContrasenaCambia = onConfirmarContrasenaCambia,
                        onAlternarVisibilidad = onAlternarVisibilidad,
                        onRestablecerContrasena = onRestablecerContrasena
                    )

                    PasoRecuperacion.EXITO -> PasoExito(
                        onVolverALogin = onVolverALogin
                    )
                }

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
            }
        }
    }
}

@Composable
private fun PasoSolicitud(
    uiState: RecuperarContrasenaUiState,
    onIdentificadorCambia: (String) -> Unit,
    onSolicitarCodigo: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)) {
        Text(
            text = stringResource(R.string.recuperar_paso1_instrucciones),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CampoTexto(
            valor = uiState.identificador,
            onValorCambia = onIdentificadorCambia,
            etiqueta = stringResource(R.string.recuperar_campo_identificador),
            icono = R.drawable.ic_persona,
            error = uiState.errorIdentificador?.texto(),
            capitalizacion = KeyboardCapitalization.None,
            tipoTeclado = KeyboardType.Email
        )

        BotonPrincipal(
            texto = stringResource(R.string.recuperar_btn_solicitar_codigo),
            onClick = onSolicitarCodigo,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PasoVerificacion(
    uiState: RecuperarContrasenaUiState,
    onCodigoCambia: (String) -> Unit,
    onVerificarCodigo: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)) {
        Text(
            text = stringResource(R.string.recuperar_paso2_instrucciones),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (uiState.codigoGenerado.isNotEmpty()) {
            TarjetaSeccion(
                titulo = "Simulación de envío"
            ) {
                Text(
                    text = stringResource(R.string.recuperar_codigo_demo_nota, uiState.codigoGenerado),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        CampoTexto(
            valor = uiState.codigoVerificacion,
            onValorCambia = onCodigoCambia,
            etiqueta = stringResource(R.string.recuperar_campo_codigo),
            icono = R.drawable.ic_check,
            error = uiState.errorCodigo?.texto(),
            tipoTeclado = KeyboardType.Number
        )

        BotonPrincipal(
            texto = stringResource(R.string.recuperar_btn_verificar_codigo),
            onClick = onVerificarCodigo,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PasoNuevaContrasena(
    uiState: RecuperarContrasenaUiState,
    onNuevaContrasenaCambia: (String) -> Unit,
    onConfirmarContrasenaCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onRestablecerContrasena: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)) {
        Text(
            text = stringResource(R.string.recuperar_paso3_instrucciones),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CampoContrasena(
            valor = uiState.nuevaContrasena,
            onValorCambia = onNuevaContrasenaCambia,
            etiqueta = stringResource(R.string.recuperar_campo_nueva),
            visible = uiState.contrasenaVisible,
            onAlternarVisible = onAlternarVisibilidad,
            error = uiState.errorNuevaContrasena?.texto()
        )

        CampoContrasena(
            valor = uiState.confirmarContrasena,
            onValorCambia = onConfirmarContrasenaCambia,
            etiqueta = stringResource(R.string.recuperar_campo_confirmar),
            visible = uiState.contrasenaVisible,
            onAlternarVisible = onAlternarVisibilidad,
            error = uiState.errorConfirmarContrasena?.texto(),
            onListo = onRestablecerContrasena
        )

        BotonPrincipal(
            texto = stringResource(R.string.recuperar_btn_restablecer),
            onClick = onRestablecerContrasena,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PasoExito(
    onVolverALogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.espacioGrande),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.espacioGrande)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(Dimens.espacioMedio)
        )

        Text(
            text = stringResource(R.string.recuperar_exito_titulo),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.recuperar_exito_mensaje),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        BotonPrincipal(
            texto = stringResource(R.string.recuperar_btn_ir_login),
            onClick = onVolverALogin,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ErrorRecuperacion.texto(): String = stringResource(
    when (this) {
        ErrorRecuperacion.CAMPO_VACIO -> R.string.error_campo_obligatorio
        ErrorRecuperacion.IDENTIFICADOR_INVALIDO -> R.string.error_identificador_invalido
        ErrorRecuperacion.USUARIO_NO_ENCONTRADO -> R.string.error_usuario_no_encontrado
        ErrorRecuperacion.CODIGO_INVALIDO -> R.string.error_codigo_invalido
        ErrorRecuperacion.CONTRASENA_DEBIL -> R.string.error_contrasena_debil
        ErrorRecuperacion.CONTRASENAS_DISTINTAS -> R.string.error_contrasenas_distintas
        ErrorRecuperacion.GENERICO -> R.string.error_generico
    }
)

@Preview(showBackground = true)
@Composable
private fun RecuperarContrasenaSolicitudPreview() {
    PagoFlexTheme {
        RecuperarContrasenaContent(
            uiState = RecuperarContrasenaUiState(),
            onIdentificadorCambia = {},
            onCodigoCambia = {},
            onNuevaContrasenaCambia = {},
            onConfirmarContrasenaCambia = {},
            onAlternarVisibilidad = {},
            onSolicitarCodigo = {},
            onVerificarCodigo = {},
            onRestablecerContrasena = {},
            onVolverAPasoAnterior = {},
            onVolverALogin = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecuperarContrasenaVerificacionPreview() {
    PagoFlexTheme {
        RecuperarContrasenaContent(
            uiState = RecuperarContrasenaUiState(
                paso = PasoRecuperacion.VERIFICACION_CODIGO,
                codigoGenerado = "654321"
            ),
            onIdentificadorCambia = {},
            onCodigoCambia = {},
            onNuevaContrasenaCambia = {},
            onConfirmarContrasenaCambia = {},
            onAlternarVisibilidad = {},
            onSolicitarCodigo = {},
            onVerificarCodigo = {},
            onRestablecerContrasena = {},
            onVolverAPasoAnterior = {},
            onVolverALogin = {}
        )
    }
}
