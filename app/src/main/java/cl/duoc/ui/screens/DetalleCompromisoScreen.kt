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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.Usuario
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.theme.Dimens
import cl.duoc.util.Fechas
import cl.duoc.viewmodel.DetalleCompromisoViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompromisoScreen(
    idCompromiso: Long,
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: DetalleCompromisoViewModel = viewModel()
) {
    val detalle by viewModel.detalle.collectAsStateWithLifecycle()
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))

    LaunchedEffect(idCompromiso, usuario.id) {
        viewModel.cargar(idCompromiso, usuario.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detalle_titulo)) },
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
                val d = detalle
                if (d == null) {
                    Text(
                        text = stringResource(R.string.error_sin_permiso),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = Dimens.espacioGrande)
                    )
                } else {
                    val c = d.compromiso

                    TarjetaSeccion(titulo = c.empresaNombre) {
                        FilaDetalle(stringResource(R.string.detalle_concepto), c.concepto)
                        c.textoCuota?.let { FilaDetalle(stringResource(R.string.detalle_cuota), it) }
                        FilaDetalle(
                            stringResource(R.string.detalle_vencimiento),
                            Fechas.aFechaCorta(c.fechaVencimiento)
                        )
                        FilaDetalle(stringResource(R.string.detalle_estado), c.estadoNombre)
                        Text(
                            text = stringResource(R.string.compromiso_folio, c.folio),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (!c.esAnulado) {
                            HorizontalDivider()
                            FilaDetalle(stringResource(R.string.detalle_monto), formatoMoneda.format(c.monto))
                            if (c.recargoAplicado > 0) {
                                FilaDetalle(
                                    stringResource(R.string.detalle_recargo),
                                    formatoMoneda.format(c.recargoAplicado)
                                )
                            }
                            if (c.esVencido) {
                                FilaDetalle(stringResource(R.string.detalle_dias_atraso), d.diasAtraso.toString())
                            }
                            FilaDetalle(
                                etiqueta = stringResource(
                                    if (c.esPagado) R.string.comprobante_monto_pagado else R.string.detalle_total_pagar
                                ),
                                valor = formatoMoneda.format(c.montoTotal),
                                destacado = true
                            )
                        }
                    }

                    d.pago?.let { pago ->
                        TarjetaSeccion(titulo = stringResource(R.string.comprobante_titulo)) {
                            FilaDetalle(stringResource(R.string.comprobante_numero), pago.numeroComprobante)
                            FilaDetalle(
                                stringResource(R.string.comprobante_fecha_hora),
                                Fechas.aFechaHoraCorta(pago.fechaHora)
                            )
                            FilaDetalle(stringResource(R.string.comprobante_canal), pago.canalNombre)
                            FilaDetalle(stringResource(R.string.pago_medio), pago.medioPagoNombre)
                            Text(
                                text = stringResource(
                                    if (pago.aTiempo) R.string.detalle_a_tiempo else R.string.detalle_con_atraso
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    d.reporte?.let { reporte ->
                        TarjetaSeccion(titulo = stringResource(R.string.reporte_numero, reporte.numero)) {
                            FilaDetalle(stringResource(R.string.reporte_motivo_label), reporte.motivoNombre)
                            FilaDetalle(stringResource(R.string.detalle_estado), reporte.estadoNombre)
                            FilaDetalle(
                                stringResource(R.string.comprobante_fecha_hora),
                                Fechas.aFechaHoraCorta(reporte.fechaCreacion)
                            )
                            reporte.comentario?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                            HorizontalDivider()
                            Text(
                                text = stringResource(R.string.reporte_respuesta_titulo),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = reporte.respuesta ?: stringResource(R.string.reporte_sin_respuesta),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    TarjetaSeccion(titulo = stringResource(R.string.detalle_contacto_empresa)) {
                        Text(c.empresaCorreo, style = MaterialTheme.typography.bodyMedium)
                        Text(c.empresaTelefono, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String, destacado: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
    }
}