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
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.viewmodel.HistorialPagosViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistorialPagosScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: HistorialPagosViewModel = viewModel()
) {
    val pagos by viewModel.pagos.collectAsStateWithLifecycle()
    val pagoSeleccionado by viewModel.pagoSeleccionado.collectAsStateWithLifecycle()

    LaunchedEffect(usuario.id) {
        viewModel.cargarHistorial(usuario.id)
    }

    HistorialPagosContent(
        pagos = pagos,
        pagoSeleccionado = pagoSeleccionado,
        onVolver = onVolver,
        onSeleccionarPago = viewModel::seleccionarPago
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPagosContent(
    pagos: List<Pago>,
    pagoSeleccionado: Pago?,
    onVolver: () -> Unit,
    onSeleccionarPago: (Pago?) -> Unit
) {
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))

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
                if (pagos.isEmpty()) {
                    Text(
                        text = stringResource(R.string.historial_vacio),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = Dimens.espacioGrande)
                    )
                } else {
                    pagos.forEach { pago ->
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
                                    text = "${pago.concepto} · ${pago.folioCompromiso}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Comprobante: ${pago.numeroComprobante} · ${pago.fechaHora}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    pagoSeleccionado?.let { pago ->
        AlertDialog(
            onDismissRequest = { onSeleccionarPago(null) },
            title = { Text(text = "Comprobante de pago") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "N.º Comprobante: ${pago.numeroComprobante}",
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    Text(text = "Empresa: ${pago.empresaNombre}")
                    Text(text = "Concepto: ${pago.concepto}")
                    Text(text = "Folio: ${pago.folioCompromiso}")
                    Text(
                        text = "Monto pagado: ${formatoMoneda.format(pago.montoPagado)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "Medio de pago: ${pago.medioPagoNombre}")
                    Text(text = "Canal: ${pago.canalNombre}")
                    Text(text = "Fecha y hora: ${pago.fechaHora}")
                }
            },
            confirmButton = {
                BotonPrincipal(
                    texto = "Cerrar",
                    onClick = { onSeleccionarPago(null) }
                )
            }
        )
    }
}
