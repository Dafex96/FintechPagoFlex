package cl.duoc.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cl.duoc.R
import cl.duoc.data.ConvertidorImagen
import cl.duoc.model.RolCodigo
import cl.duoc.model.Usuario
import cl.duoc.permisos.Permisos
import cl.duoc.ui.components.AvatarCircular
import cl.duoc.ui.components.BotonPrincipal
import cl.duoc.ui.components.BotonSecundario
import cl.duoc.ui.components.FilaDato
import cl.duoc.ui.components.TarjetaSeccion
import cl.duoc.ui.theme.Dimens
import cl.duoc.ui.theme.PagoFlexTheme
import cl.duoc.util.Fechas
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    onEditarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onFotoTomada: (Bitmap) -> Boolean,
    onImagenElegida: (Uri) -> Boolean,
    onQuitarFoto: () -> Boolean
) {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val textoFotoGuardada = stringResource(R.string.foto_guardada)
    val textoFotoQuitada = stringResource(R.string.foto_quitada)
    val textoErrorLeer = stringResource(R.string.foto_error_leer)
    val textoSinCamara = stringResource(R.string.foto_sin_camara)
    val textoSinGaleria = stringResource(R.string.foto_sin_galeria)
    val textoCamaraDenegada = stringResource(R.string.foto_permiso_camara_denegado)
    val textoGaleriaDenegada = stringResource(R.string.foto_permiso_galeria_denegado)
    val textoAjustes = stringResource(R.string.foto_btn_ajustes)

    fun avisar(mensaje: String) {
        scope.launch { snackbarHostState.showSnackbar(mensaje) }
    }

    fun avisarPermisoDenegado(mensaje: String) {
        scope.launch {
            val resultado = snackbarHostState.showSnackbar(
                message = mensaje,
                actionLabel = textoAjustes,
                duration = SnackbarDuration.Long
            )
            if (resultado == SnackbarResult.ActionPerformed) Permisos.abrirAjustes(context)
        }
    }

    val lanzadorCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { foto: Bitmap? ->
        if (foto != null) avisar(if (onFotoTomada(foto)) textoFotoGuardada else textoErrorLeer)
    }

    fun abrirCamara() {
        try {
            lanzadorCamara.launch(null)
        } catch (e: ActivityNotFoundException) {
            avisar(textoSinCamara)
        }
    }

    val pedirPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->
        if (concedido) abrirCamara() else avisarPermisoDenegado(textoCamaraDenegada)
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val uri = resultado.data?.data
        if (resultado.resultCode == Activity.RESULT_OK && uri != null) {
            avisar(if (onImagenElegida(uri)) textoFotoGuardada else textoErrorLeer)
        }
    }

    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            lanzadorGaleria.launch(intent)
        } catch (e: ActivityNotFoundException) {
            avisar(textoSinGaleria)
        }
    }

    val pedirPermisoImagenes = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->
        if (concedido) abrirGaleria() else avisarPermisoDenegado(textoGaleriaDenegada)
    }

    val foto = remember(usuario.avatar) {
        usuario.avatar?.let { bytes -> ConvertidorImagen.aBitmap(bytes)?.asImageBitmap() }
    }

    PerfilContent(
        usuario = usuario,
        foto = foto,
        snackbarHostState = snackbarHostState,
        onVolver = onVolver,
        onEditarPerfil = onEditarPerfil,
        onCerrarSesion = onCerrarSesion,
        onTomarFoto = {
            if (Permisos.tiene(context, Permisos.CAMARA)) abrirCamara()
            else pedirPermisoCamara.launch(Permisos.CAMARA)
        },
        onAbrirGaleria = {
            if (Permisos.tiene(context, Permisos.IMAGENES)) abrirGaleria()
            else pedirPermisoImagenes.launch(Permisos.IMAGENES)
        },
        onQuitarFoto = { if (onQuitarFoto()) avisar(textoFotoQuitada) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilContent(
    usuario: Usuario,
    foto: ImageBitmap?,
    snackbarHostState: SnackbarHostState,
    onVolver: () -> Unit,
    onEditarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onTomarFoto: () -> Unit,
    onAbrirGaleria: () -> Unit,
    onQuitarFoto: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_perfil)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_volver),
                            contentDescription = stringResource(R.string.cd_volver)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    .padding(horizontal = Dimens.espacioPantalla, vertical = Dimens.espacioGrande),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                AvatarCircular(
                    iniciales = usuario.iniciales,
                    descripcion = stringResource(R.string.foto_cd_avatar, usuario.nombreCompleto),
                    foto = foto
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = usuario.nombreCompleto, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = nombreDeRol(usuario.rolCodigo),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                TarjetaSeccion(titulo = stringResource(R.string.foto_cambiar)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                        BotonSecundario(
                            texto = stringResource(R.string.foto_btn_camara),
                            onClick = onTomarFoto,
                            icono = R.drawable.ic_camara,
                            modifier = Modifier.weight(1f)
                        )
                        BotonSecundario(
                            texto = stringResource(R.string.foto_btn_galeria),
                            onClick = onAbrirGaleria,
                            icono = R.drawable.ic_galeria,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (usuario.avatar != null) {
                        BotonSecundario(
                            texto = stringResource(R.string.foto_btn_quitar),
                            onClick = onQuitarFoto,
                            icono = R.drawable.ic_eliminar,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Text(
                        text = stringResource(R.string.foto_privacidad),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TarjetaSeccion(titulo = stringResource(R.string.seccion_sobre_mi)) {
                    FilaDato(
                        etiqueta = stringResource(R.string.perfil_rut),
                        valor = usuario.rut,
                        icono = R.drawable.ic_persona
                    )
                    FilaDato(
                        etiqueta = stringResource(R.string.perfil_correo),
                        valor = usuario.correo,
                        icono = R.drawable.ic_correo
                    )
                    FilaDato(
                        etiqueta = stringResource(R.string.perfil_telefono),
                        valor = usuario.telefono ?: stringResource(R.string.perfil_sin_dato),
                        icono = R.drawable.ic_telefono
                    )

                    when (usuario.rolCodigo) {
                        RolCodigo.USUARIO_FINAL -> {
                            usuario.comunaNombre?.let {
                                FilaDato(stringResource(R.string.perfil_comuna), it, R.drawable.ic_ubicacion)
                            }
                            usuario.fechaIncorporacion?.let {
                                FilaDato(
                                    etiqueta = stringResource(R.string.perfil_etiqueta_desde),
                                    valor = Fechas.aFechaCorta(it),
                                    icono = R.drawable.ic_calendario
                                )
                            }
                        }
                        RolCodigo.AGENTE -> {
                            usuario.areaNombre?.let {
                                FilaDato(stringResource(R.string.perfil_area), it, R.drawable.ic_empresa)
                            }
                        }
                        RolCodigo.EJECUTIVO -> {
                            usuario.empresaNombre?.let {
                                FilaDato(stringResource(R.string.perfil_empresa), it, R.drawable.ic_empresa)
                            }
                            usuario.cargo?.let {
                                FilaDato(stringResource(R.string.perfil_cargo), it, R.drawable.ic_persona)
                            }
                        }
                    }
                }

                BotonPrincipal(
                    texto = stringResource(R.string.editar_btn_editar),
                    onClick = onEditarPerfil,
                    modifier = Modifier.fillMaxWidth()
                )
                BotonSecundario(
                    texto = stringResource(R.string.btn_cerrar_sesion),
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Dimens.espacioChico))
            }
        }
    }
}

private val camila = Usuario(
    id = 1, rut = "15834207-3", nombres = "Camila Andrea", apellidos = "Rojas Peña",
    correo = "crojas@correo.ejemplo", rolId = 1, rolCodigo = RolCodigo.USUARIO_FINAL,
    telefono = "+56 9 0000 0101", comunaNombre = "Maipú", fechaIncorporacion = "2023-03-14"
)

private val paula = Usuario(
    id = 14, rut = "14629853-2", nombres = "Paula Andrea", apellidos = "Saavedra Núñez",
    correo = "psaavedra@altosmaipo.ejemplo", rolId = 3, rolCodigo = RolCodigo.EJECUTIVO,
    empresaNombre = "Altos del Maipo", cargo = "Ejecutiva de cobranza"
)

@Preview(showBackground = true, heightDp = 1200, name = "Usuario final")
@Composable
private fun PerfilUsuarioFinalPreview() {
    PagoFlexTheme {
        PerfilContent(
            usuario = camila, foto = null, snackbarHostState = remember { SnackbarHostState() },
            onVolver = {}, onEditarPerfil = {}, onCerrarSesion = {},
            onTomarFoto = {}, onAbrirGaleria = {}, onQuitarFoto = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1100, name = "Ejecutivo (sin teléfono)")
@Composable
private fun PerfilEjecutivoPreview() {
    PagoFlexTheme {
        PerfilContent(
            usuario = paula, foto = null, snackbarHostState = remember { SnackbarHostState() },
            onVolver = {}, onEditarPerfil = {}, onCerrarSesion = {},
            onTomarFoto = {}, onAbrirGaleria = {}, onQuitarFoto = {}
        )
    }
}