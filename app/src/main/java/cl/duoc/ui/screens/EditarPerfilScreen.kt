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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.Comuna
import cl.duoc.model.EditarPerfilUiState
import cl.duoc.model.ErrorPerfil
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.BotonSecundario
import cl.duoc.ui.components.CampoTexto
import cl.duoc.ui.components.FilaDato
import cl.duoc.ui.components.SelectorDesplegable
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.viewmodel.EditarPerfilViewModel

@Composable
fun EditarPerfilScreen(
    onPerfilGuardado: (Usuario) -> Unit,
    onVolver: () -> Unit,
    viewModel: EditarPerfilViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditarPerfilContent(
        uiState = uiState,
        onNombresCambia = viewModel::onNombresCambia,
        onApellidosCambia = viewModel::onApellidosCambia,
        onCorreoCambia = viewModel::onCorreoCambia,
        onTelefonoCambia = viewModel::onTelefonoCambia,
        onComunaSeleccionada = viewModel::onComunaSeleccionada,
        onGuardar = { viewModel.onGuardar()?.let { usuario -> onPerfilGuardado(usuario) } },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilContent(
    uiState: EditarPerfilUiState,
    onNombresCambia: (String) -> Unit,
    onApellidosCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onTelefonoCambia: (String) -> Unit,
    onComunaSeleccionada: (Long) -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.editar_titulo)) },
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
                FilaDato(
                    etiqueta = stringResource(R.string.perfil_rut),
                    valor = uiState.rut,
                    icono = R.drawable.ic_persona
                )
                Text(
                    text = stringResource(R.string.editar_rut_nota),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

                if (uiState.pideComuna) {
                    SelectorDesplegable(
                        etiqueta = stringResource(R.string.cuenta_campo_comuna),
                        opciones = uiState.comunas,
                        textoSeleccionado = uiState.comunaNombre,
                        textoDe = { comuna -> comuna.nombre },
                        onSeleccionar = { comuna -> onComunaSeleccionada(comuna.id) },
                        icono = R.drawable.ic_ubicacion,
                        error = uiState.errorComuna?.texto()
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

                BotonPrincipal(
                    texto = stringResource(R.string.editar_btn_guardar),
                    onClick = onGuardar,
                    modifier = Modifier.fillMaxWidth()
                )
                BotonSecundario(
                    texto = stringResource(R.string.btn_cancelar),
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ErrorPerfil.texto(): String = stringResource(
    when (this) {
        ErrorPerfil.CAMPO_VACIO -> R.string.error_campo_obligatorio
        ErrorPerfil.CORREO_INVALIDO -> R.string.cuenta_error_correo_invalido
        ErrorPerfil.CORREO_EXISTE -> R.string.cuenta_error_correo_existe
        ErrorPerfil.TELEFONO_INVALIDO -> R.string.cuenta_error_telefono_invalido
        ErrorPerfil.COMUNA_NO_ELEGIDA -> R.string.cuenta_error_comuna
        ErrorPerfil.GENERICO -> R.string.editar_error_guardar
    }
)

private val comunasDePrueba = listOf(Comuna(1, "Maipú"), Comuna(2, "Ñuñoa"), Comuna(3, "Quilicura"))

@Preview(showBackground = true, heightDp = 1100, name = "Usuario final")
@Composable
private fun EditarPerfilUsuarioFinalPreview() {
    PagoFlexTheme {
        EditarPerfilContent(
            uiState = EditarPerfilUiState(
                rut = "15834207-3", nombres = "Camila Andrea", apellidos = "Rojas Peña",
                correo = "crojas@correo.ejemplo", telefono = "+56 9 0000 0101",
                comunaId = 1, comunas = comunasDePrueba, pideComuna = true
            ),
            onNombresCambia = {}, onApellidosCambia = {}, onCorreoCambia = {},
            onTelefonoCambia = {}, onComunaSeleccionada = {}, onGuardar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1100, name = "Con errores (ejecutivo, sin comuna)")
@Composable
private fun EditarPerfilConErroresPreview() {
    PagoFlexTheme {
        EditarPerfilContent(
            uiState = EditarPerfilUiState(
                rut = "14629853-2", nombres = "", apellidos = "Saavedra Núñez",
                correo = "correo-malo", pideComuna = false,
                errorNombres = ErrorPerfil.CAMPO_VACIO,
                errorCorreo = ErrorPerfil.CORREO_INVALIDO,
                errorGeneral = ErrorPerfil.GENERICO
            ),
            onNombresCambia = {}, onApellidosCambia = {}, onCorreoCambia = {},
            onTelefonoCambia = {}, onComunaSeleccionada = {}, onGuardar = {}, onVolver = {}
        )
    }
}