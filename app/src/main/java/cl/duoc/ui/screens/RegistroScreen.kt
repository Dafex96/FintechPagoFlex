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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.Comuna
import cl.duoc.model.ErrorRegistro
import cl.duoc.model.RegistroUiState
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.CampoContrasena
import cl.duoc.ui.components.CampoTexto
import cl.duoc.ui.components.CasillaConTexto
import cl.duoc.ui.components.SelectorDesplegable
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegistroExitoso: (Usuario) -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegistroContent(
        uiState = uiState,
        onNombresCambia = viewModel::onNombresCambia,
        onApellidosCambia = viewModel::onApellidosCambia,
        onRutCambia = viewModel::onRutCambia,
        onCorreoCambia = viewModel::onCorreoCambia,
        onTelefonoCambia = viewModel::onTelefonoCambia,
        onComunaSeleccionada = viewModel::onComunaSeleccionada,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onConfirmarContrasenaCambia = viewModel::onConfirmarContrasenaCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onTerminosCambia = viewModel::onTerminosCambia,
        onRegistrar = { viewModel.onRegistrar()?.let { usuario -> onRegistroExitoso(usuario) } },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroContent(
    uiState: RegistroUiState,
    onNombresCambia: (String) -> Unit,
    onApellidosCambia: (String) -> Unit,
    onRutCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onTelefonoCambia: (String) -> Unit,
    onComunaSeleccionada: (Long) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onConfirmarContrasenaCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onTerminosCambia: (Boolean) -> Unit,
    onRegistrar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cuenta_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_volver),
                            contentDescription = stringResource(R.string.cd_volver)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                    .padding(horizontal = Dimens.espacioPantalla, vertical = Dimens.espacioGrande),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                Text(
                    text = stringResource(R.string.cuenta_intro),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = stringResource(R.string.cuenta_seccion_datos),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
                CampoTexto(
                    valor = uiState.nombres,
                    onValorCambia = onNombresCambia,
                    etiqueta = stringResource(R.string.cuenta_campo_nombres),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorNombres?.texto(),
                    capitalizacion = KeyboardCapitalization.Words
                )
                CampoTexto(
                    valor = uiState.apellidos,
                    onValorCambia = onApellidosCambia,
                    etiqueta = stringResource(R.string.cuenta_campo_apellidos),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorApellidos?.texto(),
                    capitalizacion = KeyboardCapitalization.Words
                )
                CampoTexto(
                    valor = uiState.rut,
                    onValorCambia = onRutCambia,
                    etiqueta = stringResource(R.string.cuenta_campo_rut),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorRut?.texto(),
                    ayuda = stringResource(R.string.cuenta_rut_ayuda),
                    capitalizacion = KeyboardCapitalization.Characters
                )
                CampoTexto(
                    valor = uiState.correo,
                    onValorCambia = onCorreoCambia,
                    etiqueta = stringResource(R.string.cuenta_campo_correo),
                    icono = R.drawable.ic_correo,
                    error = uiState.errorCorreo?.texto(),
                    tipoTeclado = KeyboardType.Email
                )
                CampoTexto(
                    valor = uiState.telefono,
                    onValorCambia = onTelefonoCambia,
                    etiqueta = stringResource(R.string.cuenta_campo_telefono),
                    icono = R.drawable.ic_telefono,
                    error = uiState.errorTelefono?.texto(),
                    ayuda = stringResource(R.string.cuenta_telefono_ayuda),
                    tipoTeclado = KeyboardType.Phone
                )
                SelectorDesplegable(
                    etiqueta = stringResource(R.string.cuenta_campo_comuna),
                    opciones = uiState.comunas,
                    textoSeleccionado = uiState.comunaNombre,
                    textoDe = { comuna -> comuna.nombre },
                    onSeleccionar = { comuna -> onComunaSeleccionada(comuna.id) },
                    icono = R.drawable.ic_ubicacion,
                    error = uiState.errorComuna?.texto()
                )

                Text(
                    text = stringResource(R.string.cuenta_seccion_acceso),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
                CampoContrasena(
                    valor = uiState.contrasena,
                    onValorCambia = onContrasenaCambia,
                    etiqueta = stringResource(R.string.cuenta_campo_contrasena),
                    visible = uiState.contrasenaVisible,
                    onAlternarVisible = onAlternarVisibilidad,
                    error = uiState.errorContrasena?.texto(),
                    ayuda = stringResource(R.string.cuenta_contrasena_ayuda),
                    accionTeclado = ImeAction.Next
                )
                CampoContrasena(
                    valor = uiState.confirmarContrasena,
                    onValorCambia = onConfirmarContrasenaCambia,
                    etiqueta = stringResource(R.string.cuenta_campo_confirmar),
                    visible = uiState.contrasenaVisible,
                    onAlternarVisible = onAlternarVisibilidad,
                    error = uiState.errorConfirmar?.texto(),
                    onListo = onRegistrar
                )

                CasillaConTexto(
                    texto = stringResource(R.string.cuenta_terminos),
                    marcado = uiState.aceptaTerminos,
                    onCambio = onTerminosCambia,
                    error = uiState.errorTerminos?.texto()
                )
                Text(
                    text = stringResource(R.string.cuenta_privacidad_nota),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    texto = stringResource(R.string.cuenta_btn_crear),
                    onClick = onRegistrar,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ErrorRegistro.texto(): String = stringResource(
    when (this) {
        ErrorRegistro.CAMPO_VACIO -> R.string.error_campo_obligatorio
        ErrorRegistro.RUT_INVALIDO -> R.string.error_rut_invalido
        ErrorRegistro.RUT_EXISTE -> R.string.cuenta_error_rut_existe
        ErrorRegistro.CORREO_INVALIDO -> R.string.cuenta_error_correo_invalido
        ErrorRegistro.CORREO_EXISTE -> R.string.cuenta_error_correo_existe
        ErrorRegistro.TELEFONO_INVALIDO -> R.string.cuenta_error_telefono_invalido
        ErrorRegistro.COMUNA_NO_ELEGIDA -> R.string.cuenta_error_comuna
        ErrorRegistro.CONTRASENA_DEBIL -> R.string.cuenta_error_contrasena_debil
        ErrorRegistro.CONTRASENAS_DISTINTAS -> R.string.cuenta_error_contrasenas_distintas
        ErrorRegistro.TERMINOS_NO_ACEPTADOS -> R.string.cuenta_error_terminos
        ErrorRegistro.GENERICO -> R.string.cuenta_error_generico
    }
)

private val comunasDePrueba = listOf(Comuna(1, "Maipú"), Comuna(2, "Ñuñoa"), Comuna(3, "Quilicura"))

@Preview(showBackground = true, heightDp = 1500, name = "Vacío")
@Composable
private fun RegistroVacioPreview() {
    PagoFlexTheme {
        RegistroContent(
            uiState = RegistroUiState(comunas = comunasDePrueba),
            onNombresCambia = {}, onApellidosCambia = {}, onRutCambia = {}, onCorreoCambia = {},
            onTelefonoCambia = {}, onComunaSeleccionada = {}, onContrasenaCambia = {},
            onConfirmarContrasenaCambia = {}, onAlternarVisibilidad = {}, onTerminosCambia = {},
            onRegistrar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1800, name = "Con errores")
@Composable
private fun RegistroConErroresPreview() {
    PagoFlexTheme {
        RegistroContent(
            uiState = RegistroUiState(
                comunas = comunasDePrueba,
                nombres = "Ana",
                rut = "12345678-9",
                errorApellidos = ErrorRegistro.CAMPO_VACIO,
                errorRut = ErrorRegistro.RUT_INVALIDO,
                errorCorreo = ErrorRegistro.CORREO_EXISTE,
                errorComuna = ErrorRegistro.COMUNA_NO_ELEGIDA,
                errorContrasena = ErrorRegistro.CONTRASENA_DEBIL,
                errorConfirmar = ErrorRegistro.CONTRASENAS_DISTINTAS,
                errorTerminos = ErrorRegistro.TERMINOS_NO_ACEPTADOS,
                errorGeneral = ErrorRegistro.GENERICO
            ),
            onNombresCambia = {}, onApellidosCambia = {}, onRutCambia = {}, onCorreoCambia = {},
            onTelefonoCambia = {}, onComunaSeleccionada = {}, onContrasenaCambia = {},
            onConfirmarContrasenaCambia = {}, onAlternarVisibilidad = {}, onTerminosCambia = {},
            onRegistrar = {}, onVolver = {}
        )
    }
}