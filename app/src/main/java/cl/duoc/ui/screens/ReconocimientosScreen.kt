package cl.duoc.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.Reconocimiento
import cl.duoc.model.ReconocimientoItem
import cl.duoc.model.TipoProgreso
import cl.duoc.model.Usuario
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.viewmodel.ReconocimientosUiState
import cl.duoc.viewmodel.ReconocimientosViewModel

@Composable
fun ReconocimientosScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: ReconocimientosViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(usuario.id) {
        viewModel.cargar(usuario.id)
    }

    ReconocimientosContent(
        estado = estado,
        onVolver = onVolver,
        onAbrirDetalle = viewModel::abrirDetalle,
        onCerrarDetalle = viewModel::cerrarDetalle,
        onCerrarCelebracion = viewModel::cerrarCelebracion
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReconocimientosContent(
    estado: ReconocimientosUiState,
    onVolver: () -> Unit,
    onAbrirDetalle: (ReconocimientoItem) -> Unit,
    onCerrarDetalle: () -> Unit,
    onCerrarCelebracion: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recon_titulo)) },
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
                    .padding(horizontal = Dimens.espacioPantalla, vertical = Dimens.espacioMedio),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                when {
                    estado.cargando -> EstadoCargando()
                    estado.hayError -> TextoError()
                    else -> {
                        ResumenReconocimientos(estado)

                        EncabezadoSeccion(stringResource(R.string.recon_seccion_obtenidos))
                        if (estado.obtenidos.isEmpty()) {
                            TarjetaVacia()
                        } else {
                            estado.obtenidos.forEach { item ->
                                TarjetaObtenida(item = item, onClick = { onAbrirDetalle(item) })
                            }
                        }

                        if (estado.porObtener.isNotEmpty()) {
                            Spacer(Modifier.height(Dimens.espacioChico))
                            EncabezadoSeccion(stringResource(R.string.recon_seccion_por_obtener))
                            estado.porObtener.forEach { item ->
                                TarjetaPorObtener(item = item, onClick = { onAbrirDetalle(item) })
                            }
                        }

                        Text(
                            text = stringResource(R.string.recon_nota_simbolico),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = Dimens.espacioChico)
                        )
                    }
                }
            }
        }
    }

    estado.detalle?.let { DialogoDetalle(item = it, onCerrar = onCerrarDetalle) }

    if (estado.nuevos.isNotEmpty()) {
        DialogoCelebracion(nuevos = estado.nuevos, onCerrar = onCerrarCelebracion)
    }
}

@Composable
private fun ResumenReconocimientos(estado: ReconocimientosUiState) {
    val obtenidos = estado.obtenidos.size
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = EstilosTarjeta.forma,
        colors = EstilosTarjeta.coloresDestacada()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            Text(
                text = stringResource(R.string.recon_resumen_cantidad, obtenidos, estado.total),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = pluralStringResource(R.plurals.recon_cantidad_obtenidos, obtenidos, obtenidos),
                style = MaterialTheme.typography.titleMedium
            )
            LinearProgressIndicator(
                progress = { if (estado.total > 0) obtenidos.toFloat() / estado.total else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .semantics {
                        contentDescription = "$obtenidos / ${estado.total}"
                    },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Text(
                text = stringResource(R.string.recon_intro),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EncabezadoSeccion(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.semantics { heading() }
    )
}

@Composable
private fun TarjetaObtenida(item: ReconocimientoItem, onClick: () -> Unit) {
    val descripcionAccesible = stringResource(R.string.cd_recon_icono_obtenido, item.nombre)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = EstilosTarjeta.forma,
        colors = EstilosTarjeta.colores(),
        border = EstilosTarjeta.borde()
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.espacioMedio)
                .semantics(mergeDescendants = true) { contentDescription = descripcionAccesible },
            horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Insignia(obtenido = true)
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                item.motivo?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
                item.fechaObtencion?.let {
                    Text(
                        text = stringResource(R.string.recon_obtenido_el, it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaPorObtener(item: ReconocimientoItem, onClick: () -> Unit) {
    val descripcionAccesible = stringResource(R.string.cd_recon_icono_pendiente, item.nombre)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = EstilosTarjeta.forma,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Dimens.bordeTarjeta, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.espacioMedio)
                .semantics(mergeDescendants = true) { contentDescription = descripcionAccesible },
            horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Insignia(obtenido = false)
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BloqueProgreso(item)
            }
        }
    }
}

@Composable
private fun BloqueProgreso(item: ReconocimientoItem) {
    if (item.tipoProgreso == TipoProgreso.NINGUNO) return
    val textoProgreso = when (item.tipoProgreso) {
        TipoProgreso.PAGOS -> stringResource(R.string.recon_progreso_pagos, item.progresoActual, item.progresoMeta)
        else -> stringResource(R.string.recon_progreso_meses, item.progresoActual, item.progresoMeta)
    }
    val descripcion = stringResource(R.string.cd_recon_progreso, item.progresoActual, item.progresoMeta)
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)) {
        LinearProgressIndicator(
            progress = { item.fraccionProgreso },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .semantics { contentDescription = descripcion },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = textoProgreso,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Insignia(obtenido: Boolean) {
    val fondo = if (obtenido) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contenido = if (obtenido) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(Dimens.tamanoIconoGrande + Dimens.espacioMini)
            .clip(CircleShape)
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = if (obtenido) R.drawable.ic_check else R.drawable.ic_candado),
            contentDescription = null,
            tint = contenido,
            modifier = Modifier.size(Dimens.tamanoIcono + Dimens.espacioChico)
        )
    }
}

@Composable
private fun TarjetaVacia() {
    TarjetaSeccion(titulo = stringResource(R.string.recon_logros_titulo)) {
        Text(
            text = stringResource(R.string.recon_vacio),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EstadoCargando() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(Dimens.espacioMedio))
        Text(stringResource(R.string.recon_cargando), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TextoError() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = EstilosTarjeta.forma,
        colors = EstilosTarjeta.coloresError()
    ) {
        Text(
            text = stringResource(R.string.recon_error_cargar),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(Dimens.espacioMedio)
        )
    }
}

@Composable
private fun DialogoDetalle(item: ReconocimientoItem, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(item.nombre) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                if (item.obtenido) {
                    Text(
                        stringResource(R.string.recon_detalle_como_lo_obtuviste),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(item.motivo.orEmpty(), style = MaterialTheme.typography.bodyLarge)
                    item.fechaObtencion?.let {
                        Text(
                            stringResource(R.string.recon_obtenido_el, it),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        stringResource(R.string.recon_detalle_como_obtenerlo),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(item.descripcion, style = MaterialTheme.typography.bodyLarge)
                    BloqueProgreso(item)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar, modifier = Modifier.heightIn(min = Dimens.areaTactilMinima)) {
                Text(stringResource(R.string.recon_btn_cerrar))
            }
        }
    )
}

@Composable
private fun DialogoCelebracion(nuevos: List<Reconocimiento>, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(stringResource(R.string.recon_nuevo_titulo)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                nuevos.forEach {
                    Text(
                        text = stringResource(R.string.recon_nuevo_texto, it.nombre),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar, modifier = Modifier.heightIn(min = Dimens.areaTactilMinima)) {
                Text(stringResource(R.string.recon_btn_seguir))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ReconocimientosPreview() {
    PagoFlexTheme {
        ReconocimientosContent(
            estado = ReconocimientosUiState(
                cargando = false,
                obtenidos = listOf(
                    ReconocimientoItem(
                        id = 1, nombre = "Primer pago a tiempo",
                        descripcion = "Pagaste un compromiso antes o el mismo día de su vencimiento.",
                        obtenido = true, fechaObtencion = "12-09-2026",
                        motivo = "Pagaste a tiempo tu compromiso con Crédito Andino."
                    )
                ),
                porObtener = listOf(
                    ReconocimientoItem(
                        id = 2, nombre = "Persona puntual",
                        descripcion = "Acumulaste 10 pagos a tiempo en tu historial.",
                        obtenido = false, tipoProgreso = TipoProgreso.PAGOS,
                        progresoActual = 1, progresoMeta = 10
                    ),
                    ReconocimientoItem(
                        id = 6, nombre = "Impulsor de PagoFlex",
                        descripcion = "Una organización que recomendaste se unió a PagoFlex.",
                        obtenido = false
                    )
                )
            ),
            onVolver = {}, onAbrirDetalle = {}, onCerrarDetalle = {}, onCerrarCelebracion = {}
        )
    }
}
