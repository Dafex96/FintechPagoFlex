package cl.duoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import cl.duoc.viewmodel.MisCompromisosUiState
import cl.duoc.viewmodel.MisCompromisosViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MisCompromisosScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: MisCompromisosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(usuario.id) {
        viewModel.cargarCompromisos(usuario.id)
    }

    MisCompromisosContent(
        uiState = uiState,
        onVolver = onVolver,
        onCambiarFiltro = viewModel::cambiarFiltro,
        onSeleccionarCompromisoParaPagar = viewModel::seleccionarCompromisoParaPagar,
        onSeleccionarMedioPago = viewModel::seleccionarMedioPago,
        onConfirmarPago = viewModel::realizarPagoSimulado,
        onSeleccionarCompromisoParaReportar = viewModel::seleccionarCompromisoParaReportar,
        onSeleccionarMotivoReporte = viewModel::seleccionarMotivoReporte,
        onComentarioReporteCambia = viewModel::onComentarioReporteCambia,
        onEnviarReporte = viewModel::enviarReporte,
        onCerrarModalExito = viewModel::cerrarModalExito
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCompromisosContent(
    uiState: MisCompromisosUiState,
    onVolver: () -> Unit,
    onCambiarFiltro: (Long?) -> Unit,
    onSeleccionarCompromisoParaPagar: (Compromiso?) -> Unit,
    onSeleccionarMedioPago: (Long) -> Unit,
    onConfirmarPago: () -> Unit,
    onSeleccionarCompromisoParaReportar: (Compromiso?) -> Unit,
    onSeleccionarMotivoReporte: (Long) -> Unit,
    onComentarioReporteCambia: (String) -> Unit,
    onEnviarReporte: () -> Unit,
    onCerrarModalExito: () -> Unit
) {
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.titulo_compromisos)) },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                }

                if (uiState.compromisos.isEmpty()) {
                    Text(
                        text = stringResource(R.string.sin_datos),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = Dimens.espacioGrande)
                    )
                } else {
                    uiState.compromisos.forEach { compromiso ->
                        TarjetaCompromiso(
                            compromiso = compromiso,
                            formatoMoneda = formatoMoneda,
                            onPagar = { onSeleccionarCompromisoParaPagar(compromiso) },
                            onReportar = { onSeleccionarCompromisoParaReportar(compromiso) }
                        )
                    }
                }
            }
        }
    }

    // Modal de Pago
    uiState.compromisoParaPagar?.let { comp ->
        AlertDialog(
            onDismissRequest = { onSeleccionarCompromisoParaPagar(null) },
            title = { Text(text = "Pagar compromiso") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Vas a pagar ${comp.empresaNombre} - ${comp.concepto}")
                    Text(
                        text = "Monto a pagar: ${formatoMoneda.format(comp.montoTotal)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (comp.recargoAplicado > 0) {
                        Text(
                            text = "Incluye recargo por atraso de ${formatoMoneda.format(comp.recargoAplicado)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = "Medio de pago:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    val medios = listOf(
                        1L to "Tarjeta de débito",
                        2L to "Tarjeta de crédito",
                        3L to "Transferencia bancaria",
                        4L to "Pago simulado"
                    )
                    medios.forEach { (id, nombre) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.medioPagoSeleccionado == id,
                                onClick = { onSeleccionarMedioPago(id) }
                            )
                            Text(text = nombre, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                BotonPrincipal(
                    texto = "Confirmar pago",
                    onClick = onConfirmarPago
                )
            },
            dismissButton = {
                TextButton(onClick = { onSeleccionarCompromisoParaPagar(null) }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal de Reportar Problema
    uiState.compromisoParaReportar?.let { comp ->
        AlertDialog(
            onDismissRequest = { onSeleccionarCompromisoParaReportar(null) },
            title = { Text(text = "Reportar un problema") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Compromiso: ${comp.empresaNombre} - ${comp.concepto}")
                    Text(
                        text = "¿Qué sucedió con este cobro?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    val motivos = listOf(
                        1L to "Ya pagué por otro medio",
                        2L to "No reconozco este cobro",
                        3L to "El monto no corresponde",
                        4L to "Otro motivo"
                    )
                    motivos.forEach { (id, nombre) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.motivoReporteSeleccionado == id,
                                onClick = { onSeleccionarMotivoReporte(id) }
                            )
                            Text(text = nombre, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    OutlinedTextField(
                        value = uiState.comentarioReporte,
                        onValueChange = onComentarioReporteCambia,
                        label = { Text("Cuéntanos más (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                BotonPrincipal(
                    texto = "Enviar reporte",
                    onClick = onEnviarReporte
                )
            },
            dismissButton = {
                TextButton(onClick = { onSeleccionarCompromisoParaReportar(null) }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal de Éxito Pago
    uiState.pagoExitoso?.let { pago ->
        AlertDialog(
            onDismissRequest = onCerrarModalExito,
            title = { Text(text = "¡Pago realizado!") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Comprobante: ${pago.numeroComprobante}")
                    Text(text = "Monto pagado: ${formatoMoneda.format(pago.montoPagado)}")
                    Text(text = "Medio: ${pago.medioPagoNombre}")
                    Text(text = "Empresa: ${pago.empresaNombre}")
                }
            },
            confirmButton = {
                BotonPrincipal(
                    texto = "Aceptar",
                    onClick = onCerrarModalExito
                )
            }
        )
    }

    // Modal de Éxito Reporte
    uiState.reporteExitoso?.let { reporte ->
        AlertDialog(
            onDismissRequest = onCerrarModalExito,
            title = { Text(text = "Reporte recibido") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "N.º de reporte: ${reporte.numero}")
                    Text(text = "Tu caso ha sido registrado y el compromiso quedó en revisión.")
                }
            },
            confirmButton = {
                BotonPrincipal(
                    texto = "Aceptar",
                    onClick = onCerrarModalExito
                )
            }
        )
    }
}

@Composable
private fun TarjetaCompromiso(
    compromiso: Compromiso,
    formatoMoneda: NumberFormat,
    onPagar: () -> Unit,
    onReportar: () -> Unit
) {
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
                    text = compromiso.empresaNombre,
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
                text = "Folio: ${compromiso.folio} · Vence: ${compromiso.fechaVencimiento}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatoMoneda.format(compromiso.montoTotal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (compromiso.recargoAplicado > 0) {
                        Text(
                            text = "+${formatoMoneda.format(compromiso.recargoAplicado)} recargo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (compromiso.puedePagar) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BotonSecundario(
                            texto = "Reportar",
                            onClick = onReportar
                        )
                        BotonPrincipal(
                            texto = "Pagar",
                            onClick = onPagar
                        )
                    }
                }
            }
        }
    }
}
