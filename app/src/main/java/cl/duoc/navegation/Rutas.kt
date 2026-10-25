package cl.duoc.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")
    data object Registro : Ruta("registro")
    data object RecuperarContrasena : Ruta("recuperar_contrasena")
    data object Inicio : Ruta("inicio")
    data object Compromisos : Ruta("compromisos")
    data object HistorialPagos : Ruta("historial_pagos")
    data object Perfil : Ruta("perfil")
    data object EditarPerfil : Ruta("editar_perfil")
}