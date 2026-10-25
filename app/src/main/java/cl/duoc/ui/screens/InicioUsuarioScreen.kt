package cl.duoc.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.Compromiso
import cl.duoc.model.ResumenInicioUsuario
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.BotonSecundario
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.styles.EstilosTarjeta
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
    onCerrarSesion: () -> Unit,
    viewModel: InicioUsuarioViewModel = viewModel()
) {
    val context = LocalContext.current
    val resumen by viewModel.resumen.collectAsStateWithLifecycle()

    LaunchedEffect(usuario.id) {
        viewModel.cargarDatos(usuario.id)
    }

    InicioUsuarioContent(
        usuario = usuario,
        resumen = resumen,
        onIrACompromisos = onIrACompromisos,
        onIrAHistorial = onIrAHistorial,
        onIrAPerfil = onIrAPerfil,
        onCerrarSesion = onCerrarSesion,
        onProbarNotificacion = {
            viewModel.probarNotificacionLocal()
            Toast.makeText(context, "Notificación enviada", Toast.LENGTH_SHORT).show()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioUsuarioContent(
    usuario: Usuario,
    resumen: ResumenInicioUsuario,
    onIrACompromisos: () -> Unit,
    onIrAHistorial: () -> Unit,
    onIrAPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onProbarNotificacion: () -> Unit
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
                // Tarjeta Resumen Financiero
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

                // Próximo Vencimiento
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

                // Acciones Rápidas
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
                    texto = stringResource(R.string.titulo_perfil),
                    onClick = onIrAPerfil,
                    icono = R.drawable.ic_persona,
                    modifier = Modifier.fillMaxWidth()
                )

                // Notificación Nativa Test
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
                            text = "📲 API Nativa: Notificaciones Locales (RF-11)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Prueba el envío de avisos de vencimiento directamente a la barra de estado de tu dispositivo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        BotonSecundario(
                            texto = "Enviar notificación de prueba",
                            onClick = onProbarNotificacion,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.espacioGrande))

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
            onIrACompromisos = {},
            onIrAHistorial = {},
            onIrAPerfil = {},
            onCerrarSesion = {},
            onProbarNotificacion = {}
        )
    }
}
