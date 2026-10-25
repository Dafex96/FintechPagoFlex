package cl.duoc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.Compromiso
import cl.duoc.model.EstadoCompromisoId
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.BotonSecundario
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.viewmodel.EjecutivoUiState
import cl.duoc.viewmodel.EjecutivoViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun EmpresaCompromisosScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: EjecutivoViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(usuario.id) {
        viewModel.cargarDatosEjecutivo(usuario.idEmpresa, usuario.id)
    }

    LaunchedEffect(uiState.mensajeExito) {
        uiState.mensajeExito?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensajes()
        }
    }

    LaunchedEffect(uiState.errorRegistro) {
        uiState.errorRegistro?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensajes()
        }
    }

    EmpresaCompromisosContent(
        uiState = uiState,
        onVolver = onVolver,
        onCambiarFiltro = viewModel::cambiarFiltroEstado,
        onSeleccionarCompromisoParaAnular = viewModel::seleccionarCompromisoParaAnular,
        onSeleccionarMotivoAnulacion = viewModel::seleccionarMotivoAnulacion,
        onComentarioAnulacionCambia = viewModel::onComentarioAnulacionCambia,
        onConfirmarAnulacion = { viewModel.confirmarAnulacion() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EmpresaCompromisosContent(
    uiState: EjecutivoUiState,
    onVolver: () -> Unit,
    onCambiarFiltro: (Long?) -> Unit,
    onSeleccionarCompromisoParaAnular: (Compromiso?) -> Unit,
    onSeleccionarMotivoAnulacion: (Long) -> Unit,
    onComentarioAnulacionCambia: (String) -> Unit,
    onConfirmarAnulacion: () -> Unit
) {
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.ejecutivo_titulo_compromisos, uiState.resumen.empresaNombre)) },
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
                    .padding(horizontal = Dimens.espacioPantalla, vertical = Dimens.espacioChico),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                // Filtros
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = uiState.filtroEstado == null,
                        onClick = { onCambiarFiltro(null) },
                        label = { Text("Todos") }
                    )
                    FilterChip(
                        selected = uiState.filtroEstado == EstadoCompromisoId.PENDIENTE.toLong(),
                        onClick = { onCambiarFiltro(EstadoCompromisoId.PENDIENTE.toLong()) },
                        label = { Text("Pendientes") }
                    )
                    FilterChip(
                        selected = uiState.filtroEstado == EstadoCompromisoId.VENCIDO.toLong(),
                        onClick = { onCambiarFiltro(EstadoCompromisoId.VENCIDO.toLong()) },
                        label = { Text("Vencidos") }
                    )
                    FilterChip(
                        selected = uiState.filtroEstado == EstadoCompromisoId.PAGADO.toLong(),
                        onClick = { onCambiarFiltro(EstadoCompromisoId.PAGADO.toLong()) },
                        label = { Text("Pagados") }
                    )
                }

                if (uiState.compromisos.isEmpty()) {
                    Text(
                        text = stringResource(R.string.sin_datos),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = Dimens.espacioGrande)
                    )
                } else {
                    uiState.compromisos.forEach { compromiso ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = EstilosTarjeta.forma,
                            colors = EstilosTarjeta.colores()
                        ) {
                            Column(
                                modifier = Modifier.padding(Dimens.espacioMedio),
                                verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Folio: ${compromiso.folio}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = MaterialTheme.shapes.extraSmall,
                                        color = when {
                                            compromiso.esPendiente -> MaterialTheme.colorScheme.primaryContainer
                                            compromiso.esVencido -> MaterialTheme.colorScheme.errorContainer
                                            compromiso.esPagado -> MaterialTheme.colorScheme.secondaryContainer
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ) {
                                        Text(
                                            text = compromiso.estadoNombre,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "${compromiso.concepto} ${compromiso.textoCuota?.let { "· $it" } ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "Vence: ${compromiso.fechaVencimiento}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = formatoMoneda.format(compromiso.montoTotal),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    if (!compromiso.esPagado && !compromiso.esAnulado) {
                                        BotonSecundario(
                                            texto = stringResource(R.string.btn_anular_compromiso),
                                            onClick = { onSeleccionarCompromisoParaAnular(compromiso) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.compromisoParaAnular?.let { comp ->
        AlertDialog(
            onDismissRequest = { onSeleccionarCompromisoParaAnular(null) },
            title = { Text(text = stringResource(R.string.anulacion_titulo)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Compromiso folio ${comp.folio} - ${comp.concepto}")
                    Text(
                        text = stringResource(R.string.anulacion_motivo_label),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    val motivos = listOf(
                        1L to stringResource(R.string.anulacion_motivo_repactacion),
                        2L to stringResource(R.string.anulacion_motivo_error),
                        3L to stringResource(R.string.anulacion_motivo_pagado_directo),
                        4L to stringResource(R.string.anulacion_motivo_otro)
                    )
                    motivos.forEach { (id, nombre) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.motivoAnulacionSeleccionado == id,
                                onClick = { onSeleccionarMotivoAnulacion(id) }
                            )
                            Text(text = nombre, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    OutlinedTextField(
                        value = uiState.comentarioAnulacion,
                        onValueChange = onComentarioAnulacionCambia,
                        label = { Text(stringResource(R.string.anulacion_comentario_hint)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                BotonPrincipal(
                    texto = stringResource(R.string.btn_confirmar_anulacion),
                    onClick = onConfirmarAnulacion
                )
            },
            dismissButton = {
                TextButton(onClick = { onSeleccionarCompromisoParaAnular(null) }) {
                    Text(stringResource(R.string.btn_cancelar))
                }
            }
        )
    }
}
