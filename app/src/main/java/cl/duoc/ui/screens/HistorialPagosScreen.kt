package cl.duoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import cl.duoc.model.Pago
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.SelectorDesplegable
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.util.Fechas
import cl.duoc.viewmodel.HistorialPagosViewModel
import cl.duoc.viewmodel.HistorialUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistorialPagosScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: HistorialPagosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(usuario.id) {
        viewModel.cargarHistorial(usuario.id)
    }

    HistorialPagosContent(
        uiState = uiState,
        onVolver = onVolver,
        onFiltrarEmpresa = viewModel::filtrarEmpresa,
        onFiltrarAnio = viewModel::filtrarAnio,
        onFiltrarMes = viewModel::filtrarMes,
        onSeleccionarPago = viewModel::seleccionarPago
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPagosContent(
    uiState: HistorialUiState,
    onVolver: () -> Unit,
    onFiltrarEmpresa: (String?) -> Unit,
    onFiltrarAnio: (Int?) -> Unit,
    onFiltrarMes: (Int?) -> Unit,
    onSeleccionarPago: (Pago?) -> Unit
) {
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
    val todos = stringResource(R.string.filtro_todos)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.titulo_historial)) },
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
                SelectorDesplegable(
                    etiqueta = stringResource(R.string.historial_filtro_empresa),
                    opciones = listOf<String?>(null) + uiState.empresas,
                    textoSeleccionado = uiState.empresaFiltro ?: todos,
                    textoDe = { it ?: todos },
                    onSeleccionar = onFiltrarEmpresa
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    SelectorDesplegable(
                        etiqueta = stringResource(R.string.filtro_anio),
                        opciones = listOf<Int?>(null) + uiState.anios,
                        textoSeleccionado = uiState.anioFiltro?.toString() ?: todos,
                        textoDe = { it?.toString() ?: todos },
                        onSeleccionar = onFiltrarAnio,
                        modifier = Modifier.weight(1f)
                    )
                    SelectorDesplegable(
                        etiqueta = stringResource(R.string.filtro_mes),
                        opciones = listOf<Int?>(null) + (1..12).toList(),
                        textoSeleccionado = uiState.mesFiltro?.let { Fechas.nombreMes(it) } ?: todos,
                        textoDe = { m -> m?.let { Fechas.nombreMes(it) } ?: todos },
                        onSeleccionar = onFiltrarMes,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (uiState.pagos.isEmpty() && uiState.anulados.isEmpty()) {
                    Text(
                        text = stringResource(R.string.historial_vacio),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = Dimens.espacioGrande)
                    )
                }

                uiState.pagos.forEach { pago ->
                    Card(
                        onClick = { onSeleccionarPago(pago) },
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
                                    text = pago.empresaNombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formatoMoneda.format(pago.montoPagado),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "${pago.concepto}${pago.textoCuota?.let { " · $it" } ?: ""}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${Fechas.aFechaHoraCorta(pago.fechaHora)} · ${pago.canalNombre}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(
                                    if (pago.aTiempo) R.string.detalle_a_tiempo else R.string.detalle_con_atraso
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                uiState.anulados.forEach { compromiso ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = EstilosTarjeta.forma,
                        colors = EstilosTarjeta.colores()
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.espacioMedio),
                            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                        ) {
                            Text(
                                text = compromiso.empresaNombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${compromiso.concepto} · ${compromiso.folio}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.historial_anulado_nota),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    uiState.pagoSeleccionado?.let { pago ->
        AlertDialog(
            onDismissRequest = { onSeleccionarPago(null) },
            title = { Text(text = stringResource(R.string.comprobante_titulo)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "N.º Comprobante: ${pago.numeroComprobante}",
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    Text(text = "Empresa: ${pago.empresaNombre}")
                    Text(text = "Concepto: ${pago.concepto}${pago.textoCuota?.let { " · $it" } ?: ""}")
                    Text(text = "Folio: ${pago.folioCompromiso}")
                    Text(text = "Vencimiento: ${Fechas.aFechaCorta(pago.fechaVencimiento)}")
                    Text(
                        text = "Monto pagado: ${formatoMoneda.format(pago.montoPagado)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "Medio de pago: ${pago.medioPagoNombre}")
                    Text(text = "Canal: ${pago.canalNombre}")
                    Text(text = "Fecha y hora: ${Fechas.aFechaHoraCorta(pago.fechaHora)}")
                    Text(text = "Estado: Pago confirmado")
                }
            },
            confirmButton = {
                BotonPrincipal(texto = "Cerrar", onClick = { onSeleccionarPago(null) })
            }
        )
    }
}