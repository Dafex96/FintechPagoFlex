package cl.duoc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.ui.screens.InicioScreen
import cl.duoc.ui.screens.LoginScreen
import cl.duoc.ui.screens.RegistroScreen
import cl.duoc.viewmodel.SesionViewModel

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

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
}