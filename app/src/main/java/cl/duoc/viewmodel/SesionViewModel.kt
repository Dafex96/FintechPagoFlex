package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.data.SesionPreferencias
import cl.duoc.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)
    private val sesion = SesionPreferencias(application)

    private val _usuarioActual = MutableStateFlow(recuperarSesion())
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private fun recuperarSesion(): Usuario? {
        val usuario = db.buscarUsuarioPorId(sesion.leer())?.takeIf { it.activo }
        if (usuario == null) sesion.borrar()
        return usuario
    }

    fun iniciarSesion(usuario: Usuario) {
        sesion.guardar(usuario.id)
        _usuarioActual.value = usuario
    }

    fun cerrarSesion() {
        sesion.borrar()
        _usuarioActual.value = null
    }

    fun actualizarUsuario(usuario: Usuario) {
        _usuarioActual.value = usuario
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}