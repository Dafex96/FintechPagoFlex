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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.RolCodigo
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.BotonSecundario
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.viewmodel.EjecutivoUiState
import cl.duoc.viewmodel.EjecutivoViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InicioEjecutivoScreen(
    usuario: Usuario,
    onIrACompromisosEmpresa: () -> Unit,
    onIrARegistrarCompromiso: () -> Unit,
    onIrAPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: EjecutivoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(usuario.id) {
        viewModel.cargarDatosEjecutivo(usuario.idEmpresa, usuario.id)
    }

    InicioEjecutivoContent(
        usuario = usuario,
        uiState = uiState,
        onIrACompromisosEmpresa = onIrACompromisosEmpresa,
        onIrARegistrarCompromiso = onIrARegistrarCompromiso,
        onIrAPerfil = onIrAPerfil,
        onCerrarSesion = onCerrarSesion
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioEjecutivoContent(
    usuario: Usuario,
    uiState: EjecutivoUiState,
    onIrACompromisosEmpresa: () -> Unit,
    onIrARegistrarCompromiso: () -> Unit,
    onIrAPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
    val resumen = uiState.resumen

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
                // Info de Empresa
                Text(
                    text = "Empresa: ${resumen.empresaNombre.ifEmpty { usuario.empresaNombre ?: "Empresa Cliente" }}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Resumen de Cobranza (RF-20)
                TarjetaSeccion(titulo = "Resumen de Cobranza") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Recaudado:")
                            Text(formatoMoneda.format(resumen.montoRecaudado), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Monto Pendiente:")
                            Text(formatoMoneda.format(resumen.montoPendiente), fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Monto Vencido:")
                            Text(formatoMoneda.format(resumen.montoVencido), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tasa de Cobranza:")
                            Text(String.format(Locale.forLanguageTag("es-CL"), "%.1f %%", resumen.tasaCobranza), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Acciones del Ejecutivo (RF-17, RF-19)
                Text(
                    text = "Acciones de Cobranza",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                BotonPrincipal(
                    texto = stringResource(R.string.btn_nuevo_compromiso),
                    onClick = onIrARegistrarCompromiso,
                    icono = R.drawable.ic_check,
                    modifier = Modifier.fillMaxWidth()
                )

                BotonSecundario(
                    texto = "Ver compromisos de la empresa (${resumen.totalCompromisos})",
                    onClick = onIrACompromisosEmpresa,
                    modifier = Modifier.fillMaxWidth()
                )

                BotonSecundario(
                    texto = stringResource(R.string.titulo_perfil),
                    onClick = onIrAPerfil,
                    icono = R.drawable.ic_persona,
                    modifier = Modifier.fillMaxWidth()
                )

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
private fun InicioEjecutivoPreview() {
    PagoFlexTheme {
        InicioEjecutivoContent(
            usuario = Usuario(
                id = 20, rut = "14629853-2", nombres = "Paula Andrea", apellidos = "Saavedra Núñez",
                correo = "psaavedra@altosmaipo.ejemplo", rolId = 3, rolCodigo = RolCodigo.EJECUTIVO,
                idEmpresa = 1, empresaNombre = "Altos del Maipo"
            ),
            uiState = EjecutivoUiState(
                resumen = cl.duoc.model.ResumenCobranza(
                    empresaNombre = "Altos del Maipo",
                    montoRecaudado = 450000,
                    montoPendiente = 85000,
                    montoVencido = 120000,
                    totalCompromisos = 5,
                    pagadosCount = 3,
                    pendientesCount = 1,
                    vencidosCount = 1
                )
            ),
            onIrACompromisosEmpresa = {},
            onIrARegistrarCompromiso = {},
            onIrAPerfil = {},
            onCerrarSesion = {}
        )
    }
}
