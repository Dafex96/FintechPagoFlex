package cl.duoc.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.ConvertidorImagen
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

    fun cambiarAvatarDesdeCamara(foto: Bitmap): Boolean =
        guardarAvatar(ConvertidorImagen.aBytes(foto))

    fun cambiarAvatarDesdeGaleria(uri: Uri): Boolean {
        val imagen = ConvertidorImagen.desdeUri(getApplication(), uri) ?: return false
        return guardarAvatar(ConvertidorImagen.aBytes(imagen))
    }

    fun quitarAvatar(): Boolean = guardarAvatar(null)

    private fun guardarAvatar(bytes: ByteArray?): Boolean {
        val usuario = _usuarioActual.value ?: return false
        if (db.actualizarAvatar(usuario.id, bytes) == 0) return false
        _usuarioActual.value = db.buscarUsuarioPorId(usuario.id)
        return true
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}