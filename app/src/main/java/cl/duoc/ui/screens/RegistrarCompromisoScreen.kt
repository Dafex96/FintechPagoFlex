package cl.duoc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.R
import cl.duoc.model.NuevoCompromisoEmpresa
import cl.duoc.model.Usuario
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.CampoTexto
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.theme.Dimens
import cl.duoc.viewmodel.EjecutivoUiState
import cl.duoc.viewmodel.EjecutivoViewModel

@Composable
fun RegistrarCompromisoScreen(
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
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensajes()
            onVolver()
        }
    }

    LaunchedEffect(uiState.errorRegistro) {
        uiState.errorRegistro?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensajes()
        }
    }

    RegistrarCompromisoContent(
        uiState = uiState,
        onActualizar = viewModel::actualizarNuevoCompromiso,
        onRegistrar = { viewModel.registrarCompromiso() },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarCompromisoContent(
    uiState: EjecutivoUiState,
    onActualizar: ((NuevoCompromisoEmpresa) -> NuevoCompromisoEmpresa) -> Unit,
    onRegistrar: () -> Unit,
    onVolver: () -> Unit
) {
    val nuevo = uiState.nuevoCompromiso

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.registro_titulo)) },
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
                .imePadding()
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
                TarjetaSeccion(titulo = stringResource(R.string.registro_datos_persona)) {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)) {
                        CampoTexto(
                            valor = nuevo.rutPersona,
                            onValorCambia = { r -> onActualizar { it.copy(rutPersona = r) } },
                            etiqueta = stringResource(R.string.registro_rut_persona),
                            icono = R.drawable.ic_persona,
                            capitalizacion = KeyboardCapitalization.Characters
                        )
                        CampoTexto(
                            valor = nuevo.nombresPersona,
                            onValorCambia = { n -> onActualizar { it.copy(nombresPersona = n) } },
                            etiqueta = stringResource(R.string.registro_nombre),
                            icono = R.drawable.ic_persona
                        )
                        CampoTexto(
                            valor = nuevo.apellidosPersona,
                            onValorCambia = { a -> onActualizar { it.copy(apellidosPersona = a) } },
                            etiqueta = "Apellidos",
                            icono = R.drawable.ic_persona
                        )
                        CampoTexto(
                            valor = nuevo.correoPersona,
                            onValorCambia = { c -> onActualizar { it.copy(correoPersona = c) } },
                            etiqueta = stringResource(R.string.registro_correo),
                            icono = R.drawable.ic_persona,
                            tipoTeclado = KeyboardType.Email
                        )
                        CampoTexto(
                            valor = nuevo.telefonoPersona,
                            onValorCambia = { t -> onActualizar { it.copy(telefonoPersona = t) } },
                            etiqueta = stringResource(R.string.registro_telefono),
                            icono = R.drawable.ic_persona,
                            tipoTeclado = KeyboardType.Phone
                        )
                    }
                }

                TarjetaSeccion(titulo = "Detalle del Compromiso") {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)) {
                        CampoTexto(
                            valor = nuevo.concepto,
                            onValorCambia = { c -> onActualizar { it.copy(concepto = c) } },
                            etiqueta = stringResource(R.string.registro_concepto),
                            icono = R.drawable.ic_check
                        )
                        CampoTexto(
                            valor = nuevo.monto,
                            onValorCambia = { m -> onActualizar { it.copy(monto = m) } },
                            etiqueta = stringResource(R.string.registro_monto),
                            icono = R.drawable.ic_check,
                            tipoTeclado = KeyboardType.Number
                        )
                        CampoTexto(
                            valor = nuevo.fechaVencimiento,
                            onValorCambia = { f -> onActualizar { it.copy(fechaVencimiento = f) } },
                            etiqueta = "Fecha de vencimiento (Ej: 2026-11-15)",
                            icono = R.drawable.ic_check
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                CampoTexto(
                                    valor = nuevo.numeroCuota,
                                    onValorCambia = { nc -> onActualizar { it.copy(numeroCuota = nc) } },
                                    etiqueta = stringResource(R.string.registro_numero_cuota),
                                    icono = R.drawable.ic_check,
                                    tipoTeclado = KeyboardType.Number
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CampoTexto(
                                    valor = nuevo.totalCuotas,
                                    onValorCambia = { tc -> onActualizar { it.copy(totalCuotas = tc) } },
                                    etiqueta = stringResource(R.string.registro_total_cuotas),
                                    icono = R.drawable.ic_check,
                                    tipoTeclado = KeyboardType.Number
                                )
                            }
                        }
                    }
                }

                BotonPrincipal(
                    texto = stringResource(R.string.btn_registrar_compromiso),
                    onClick = onRegistrar,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
