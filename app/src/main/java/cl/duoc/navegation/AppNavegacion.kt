package cl.duoc.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.R
import cl.duoc.ui.screens.EditarPerfilScreen
import cl.duoc.ui.screens.HistorialPagosScreen
import cl.duoc.ui.screens.InicioScreen
import cl.duoc.ui.screens.InicioUsuarioScreen
import cl.duoc.ui.screens.LoginScreen
import cl.duoc.ui.screens.MisCompromisosScreen
import cl.duoc.ui.screens.PerfilScreen
import cl.duoc.ui.screens.RecuperarContrasenaScreen
import cl.duoc.ui.screens.RegistroScreen
import cl.duoc.viewmodel.SesionViewModel

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val sesionViewModel: SesionViewModel = viewModel()
    val usuarioActual by sesionViewModel.usuarioActual.collectAsStateWithLifecycle()

    val destinoInicial = remember {
        if (usuarioActual != null) Ruta.Inicio.ruta else Ruta.Login.ruta
    }

    NavHost(navController = navController, startDestination = destinoInicial) {

        composable(Ruta.Login.ruta) {
            LoginScreen(
                onIrARegistro = { navController.navigate(Ruta.Registro.ruta) },
                onIrARecuperarContrasena = { navController.navigate(Ruta.RecuperarContrasena.ruta) },
                onLoginExitoso = { usuario ->
                    sesionViewModel.iniciarSesion(usuario)
                    navController.navigate(Ruta.Inicio.ruta) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Ruta.Registro.ruta) {
            RegistroScreen(
                onRegistroExitoso = { usuario ->
                    sesionViewModel.iniciarSesion(usuario)
                    navController.navigate(Ruta.Inicio.ruta) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Ruta.RecuperarContrasena.ruta) {
            RecuperarContrasenaScreen(
                onVolverALogin = {
                    navController.popBackStack(Ruta.Login.ruta, inclusive = false)
                }
            )
        }

        composable(Ruta.Inicio.ruta) {
            usuarioActual?.let { usuario ->
                if (usuario.esUsuarioFinal) {
                    InicioUsuarioScreen(
                        usuario = usuario,
                        onIrACompromisos = { navController.navigate(Ruta.Compromisos.ruta) },
                        onIrAHistorial = { navController.navigate(Ruta.HistorialPagos.ruta) },
                        onIrAPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                        onCerrarSesion = {
                            sesionViewModel.cerrarSesion()
                            navController.navigate(Ruta.Login.ruta) {
                                popUpTo(Ruta.Inicio.ruta) { inclusive = true }
                            }
                        }
                    )
                } else {
                    InicioScreen(
                        usuario = usuario,
                        onIrAPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                        onCerrarSesion = {
                            sesionViewModel.cerrarSesion()
                            navController.navigate(Ruta.Login.ruta) {
                                popUpTo(Ruta.Inicio.ruta) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        composable(Ruta.Compromisos.ruta) {
            usuarioActual?.let { usuario ->
                MisCompromisosScreen(
                    usuario = usuario,
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable(Ruta.HistorialPagos.ruta) {
            usuarioActual?.let { usuario ->
                HistorialPagosScreen(
                    usuario = usuario,
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable(Ruta.Perfil.ruta) {
            usuarioActual?.let { usuario ->
                PerfilScreen(
                    usuario = usuario,
                    onVolver = { navController.popBackStack() },
                    onEditarPerfil = { navController.navigate(Ruta.EditarPerfil.ruta) },
                    onCerrarSesion = {
                        sesionViewModel.cerrarSesion()
                        navController.navigate(Ruta.Login.ruta) {
                            popUpTo(Ruta.Inicio.ruta) { inclusive = true }
                        }
                    },
                    onFotoTomada = { foto -> sesionViewModel.cambiarAvatarDesdeCamara(foto) },
                    onImagenElegida = { uri -> sesionViewModel.cambiarAvatarDesdeGaleria(uri) },
                    onQuitarFoto = { sesionViewModel.quitarAvatar() }
                )
            }
        }

        composable(Ruta.EditarPerfil.ruta) {
            EditarPerfilScreen(
                onPerfilGuardado = { usuario ->
                    sesionViewModel.actualizarUsuario(usuario)
                    Toast.makeText(context, R.string.editar_guardado, Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}