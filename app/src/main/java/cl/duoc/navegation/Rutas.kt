package cl.duoc.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")
    data object Inicio : Ruta("inicio")
}