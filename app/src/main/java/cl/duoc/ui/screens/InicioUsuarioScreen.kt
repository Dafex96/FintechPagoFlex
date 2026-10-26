package cl.duoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.CategoriaCumplimiento
import cl.duoc.model.Compromiso
import cl.duoc.model.ResumenInicioUsuario
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.BotonSecundario
import cl.duoc.ui.components.SolicitudPermisoNotificaciones
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.viewmodel.InicioUsuarioViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InicioUsuarioScreen(
    usuario: Usuario,
    onIrACompromisos: () -> Unit,
    onIrAHistorial: () -> Unit,
    onIrAPerfil: () -> Unit,
    onIrAReconocimientos: () -> Unit,
    onIrAAvisos: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: InicioUsuarioViewModel = viewModel()
) {
    val resumen by viewModel.resumen.collectAsStateWithLifecycle()
    val avisosActivos by viewModel.avisosActivos.collectAsStateWithLifecycle()

    LaunchedEffect(usuario.id) {
        viewModel.cargarDatos(usuario.id)
    }

    InicioUsuarioContent(
        usuario = usuario,
        resumen = resumen,
        avisosActivos = avisosActivos,
        onIrACompromisos = onIrACompromisos,
        onIrAHistorial = onIrAHistorial,
        onIrAPerfil = onIrAPerfil,
        onIrAReconocimientos = onIrAReconocimientos,
        onIrAAvisos = onIrAAvisos,
        onCerrarSesion = onCerrarSesion
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioUsuarioContent(
    usuario: Usuario,
    resumen: ResumenInicioUsuario,
    avisosActivos: Boolean,
    onIrACompromisos: () -> Unit,
    onIrAHistorial: () -> Unit,
    onIrAPerfil: () -> Unit,
    onIrAReconocimientos: () -> Unit,
    onIrAAvisos: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.inicio_saludo, usuario.primerNombre)) },
                actions = {
                    IconButton(onClick = onIrAPerfil) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_persona),
                            contentDescription = stringResource(R.string.nav_perfil),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
                    .padding(Dimens.espacioPantalla),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioGrande)
            ) {
                TarjetaSeccion(titulo = stringResource(R.string.inicio_total_mes)) {
                    Text(
                        text = formatoMoneda.format(resumen.totalMes),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (resumen.cantidadVencidos > 0) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(top = Dimens.espacioChico)
                        ) {
                            Text(
                                text = "⚠️ Tienes ${resumen.cantidadVencidos} compromiso(s) por regularizar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = Dimens.espacioMedio, vertical = Dimens.espacioChico)
                            )
                        }
                    }
                }

                TarjetaSeccion(titulo = stringResource(R.string.cumplimiento_titulo)) {
                    val indicador = resumen.cumplimiento
                    if (!indicador.suficiente) {
                        Text(
                            text = stringResource(R.string.cumplimiento_insuficiente),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        val porcentaje = String.format(Locale.forLanguageTag("es-CL"), "%.1f", indicador.porcentaje)
                        Text(
                            text = stringResource(R.string.cumplimiento_porcentaje, porcentaje),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val categoria = when (indicador.categoria) {
                            CategoriaCumplimiento.EJEMPLAR -> stringResource(R.string.categoria_ejemplar)
                            CategoriaCumplimiento.AL_DIA -> stringResource(R.string.categoria_al_dia)
                            CategoriaCumplimiento.EN_CAMINO -> stringResource(R.string.categoria_en_camino)
                            null -> ""
                        }
                        Text(text = categoria, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = stringResource(R.string.cumplimiento_detalle, indicador.aTiempo, indicador.considerados),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                resumen.proximoCompromiso?.let { proximo ->
                    TarjetaSeccion(titulo = stringResource(R.string.inicio_proximo_vencimiento)) {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                            Text(
                                text = proximo.empresaNombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${proximo.concepto} · ${proximo.textoCuota ?: ""}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Vence: ${proximo.fechaVencimiento}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (proximo.esVencido) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatoMoneda.format(proximo.montoTotal),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                if (avisosActivos) {
                    SolicitudPermisoNotificaciones()
                }

                Text(
                    text = "Menú Principal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                BotonPrincipal(
                    texto = "Mis Compromisos (${resumen.compromisosPendientes.size + resumen.compromisosVencidos.size})",
                    onClick = onIrACompromisos,
                    icono = R.drawable.ic_check,
                    modifier = Modifier.fillMaxWidth()
                )

                BotonSecundario(
                    texto = stringResource(R.string.titulo_historial),
                    onClick = onIrAHistorial,
                    modifier = Modifier.fillMaxWidth()
                )

                BotonSecundario(
                    texto = "Mis Reconocimientos",
                    onClick = onIrAReconocimientos,
                    modifier = Modifier.fillMaxWidth()
                )

                BotonSecundario(
                    texto = stringResource(R.string.avisos_titulo),
                    onClick = onIrAAvisos,
                    icono = R.drawable.ic_calendario,
                    modifier = Modifier.fillMaxWidth()
                )

                BotonSecundario(
                    texto = stringResource(R.string.btn_cerrar_sesion),
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InicioUsuarioPreview() {
    PagoFlexTheme {
        InicioUsuarioContent(
            usuario = Usuario(
                id = 1, rut = "15834207-3", nombres = "Camila Andrea", apellidos = "Rojas Peña",
                correo = "crojas@correo.ejemplo", rolId = 1, rolCodigo = "USUARIO_FINAL"
            ),
            resumen = ResumenInicioUsuario(
                totalMes = 205000,
                cantidadVencidos = 1
            ),
            avisosActivos = true,
            onIrACompromisos = {},
            onIrAHistorial = {},
            onIrAPerfil = {},
            onIrAReconocimientos = {},
            onIrAAvisos = {},
            onCerrarSesion = {}
        )
    }
}