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
import cl.duoc.ui.screens.InicioScreen
import cl.duoc.ui.screens.LoginScreen
import cl.duoc.ui.screens.PerfilScreen
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

        composable(Ruta.Inicio.ruta) {
            usuarioActual?.let { usuario ->
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
                    Toast.makeText(context, context.getString(R.string.editar_guardado), Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}