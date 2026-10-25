package cl.duoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.AvisoAnticipacion
import cl.duoc.model.Usuario
import cl.duoc.ui.components.SolicitudPermisoNotificaciones
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.util.Fechas
import cl.duoc.viewmodel.AvisosUiState
import cl.duoc.viewmodel.AvisosViewModel

@Composable
fun AvisosScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: AvisosViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(usuario.id) { viewModel.cargar(usuario.id) }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbarHostState.showSnackbar(contexto.getString(it))
            viewModel.mensajeMostrado()
        }
    }

    AvisosContent(
        estado = estado,
        snackbarHostState = snackbarHostState,
        onVolver = onVolver,
        onCambiarActivos = viewModel::cambiarActivos,
        onCambiarAnticipacion = viewModel::cambiarAnticipacion
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvisosContent(
    estado: AvisosUiState,
    snackbarHostState: SnackbarHostState,
    onVolver: () -> Unit,
    onCambiarActivos: (Boolean) -> Unit,
    onCambiarAnticipacion: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.avisos_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_volver),
                            contentDescription = stringResource(R.string.cd_volver)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = Dimens.anchoMaximoContenido)
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.espacioPantalla, vertical = Dimens.espacioMedio),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                Text(
                    text = stringResource(R.string.avisos_intro),
                    style = MaterialTheme.typography.bodyLarge
                )

                SolicitudPermisoNotificaciones()

                TarjetaSeccion(titulo = stringResource(R.string.avisos_switch_label)) {
                    FilaInterruptor(activo = estado.activos, onCambio = onCambiarActivos)

                    if (estado.activos) {
                        Text(
                            text = stringResource(R.string.avisos_anticipacion_titulo),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Column(Modifier.selectableGroup()) {
                            AvisoAnticipacion.OPCIONES.forEach { dias ->
                                OpcionAnticipacion(
                                    dias = dias,
                                    seleccionada = dias == estado.diasAnticipacion,
                                    onElegir = { onCambiarAnticipacion(dias) }
                                )
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.avisos_desactivados_nota),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (estado.activos) {
                    VistaPreviaAviso(dias = estado.diasAnticipacion)
                }

                TarjetaSeccion(titulo = stringResource(R.string.avisos_reglas_titulo)) {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                        Text(stringResource(R.string.avisos_regla_maximo), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.avisos_regla_horario), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.avisos_regla_estados), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaInterruptor(activo: Boolean, onCambio: (Boolean) -> Unit) {
    val descripcion = stringResource(R.string.cd_avisos_switch)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.areaTactilMinima)
            .toggleable(value = activo, role = Role.Switch, onValueChange = onCambio)
            .semantics { contentDescription = descripcion },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(if (activo) R.string.avisos_switch_activo else R.string.avisos_switch_inactivo),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Switch(checked = activo, onCheckedChange = null)
    }
}

@Composable
private fun OpcionAnticipacion(dias: Int, seleccionada: Boolean, onElegir: () -> Unit) {
    val texto = pluralStringResource(R.plurals.avisos_opcion_dias, dias, dias)
    val descripcion = stringResource(
        if (seleccionada) R.string.cd_avisos_opcion_seleccionada else R.string.cd_avisos_opcion_no_seleccionada,
        texto
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.areaTactilMinima)
            .selectable(selected = seleccionada, role = Role.RadioButton, onClick = onElegir)
            .semantics { contentDescription = descripcion },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = seleccionada, onClick = null)
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = Dimens.espacioMedio)
        )
        if (dias == AvisoAnticipacion.POR_DEFECTO) {
            Text(
                text = stringResource(R.string.avisos_recomendado),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = Dimens.espacioChico)
            )
        }
    }
}

@Composable
private fun VistaPreviaAviso(dias: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = EstilosTarjeta.forma,
        colors = EstilosTarjeta.coloresDestacada()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)
        ) {
            Text(
                text = stringResource(R.string.avisos_vista_previa_titulo),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = stringResource(R.string.notif_vence_titulo),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = pluralStringResource(
                    R.plurals.notif_vence_texto, dias, dias, "Club Los Halcones", Fechas.fechaCortaEnDias(dias)
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvisosPreview() {
    PagoFlexTheme {
        AvisosContent(
            estado = AvisosUiState(activos = true, diasAnticipacion = 3),
            snackbarHostState = SnackbarHostState(),
            onVolver = {}, onCambiarActivos = {}, onCambiarAnticipacion = {}
        )
    }
}
