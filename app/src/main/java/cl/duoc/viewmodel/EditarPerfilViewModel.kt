package cl.duoc.viewmodel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.data.SesionPreferencias
import cl.duoc.model.CambiosPerfil
import cl.duoc.model.EditarPerfilUiState
import cl.duoc.model.ErrorPerfil
import cl.duoc.model.RolCodigo
import cl.duoc.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditarPerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val usuario: Usuario? = db.buscarUsuarioPorId(SesionPreferencias(application).leer())

    private val _uiState = MutableStateFlow(estadoInicial())
    val uiState: StateFlow<EditarPerfilUiState> = _uiState.asStateFlow()

    private fun estadoInicial(): EditarPerfilUiState {
        val u = usuario ?: return EditarPerfilUiState()
        val esUsuarioFinal = u.rolCodigo == RolCodigo.USUARIO_FINAL
        return EditarPerfilUiState(
            rut = u.rut,
            nombres = u.nombres,
            apellidos = u.apellidos,
            correo = u.correo,
            telefono = u.telefono ?: "",
            comunaId = u.comunaId,
            comunas = if (esUsuarioFinal) db.listarComunas() else emptyList(),
            pideComuna = esUsuarioFinal
        )
    }

    fun onNombresCambia(valor: String) {
        _uiState.update { it.copy(nombres = valor, errorNombres = null, errorGeneral = null) }
    }

    fun onApellidosCambia(valor: String) {
        _uiState.update { it.copy(apellidos = valor, errorApellidos = null, errorGeneral = null) }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }
    }

    fun onTelefonoCambia(valor: String) {
        _uiState.update { it.copy(telefono = valor, errorTelefono = null, errorGeneral = null) }
    }

    fun onComunaSeleccionada(comunaId: Long) {
        _uiState.update { it.copy(comunaId = comunaId, errorComuna = null, errorGeneral = null) }
    }

    fun onGuardar(): Usuario? {
        val actual = usuario ?: return fallo()
        val estado = _uiState.value

        val nombres = estado.nombres.trim()
        val apellidos = estado.apellidos.trim()
        val correo = estado.correo.trim()
        val telefono = estado.telefono.trim()

        val errorCorreo = try {
            when {
                correo.isBlank() -> ErrorPerfil.CAMPO_VACIO
                !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> ErrorPerfil.CORREO_INVALIDO
                db.existeCorreoDeOtro(correo, actual.id) -> ErrorPerfil.CORREO_EXISTE
                else -> null
            }
        } catch (e: Exception) {
            return fallo(e)
        }

        val errorNombres = if (nombres.isBlank()) ErrorPerfil.CAMPO_VACIO else null
        val errorApellidos = if (apellidos.isBlank()) ErrorPerfil.CAMPO_VACIO else null
        val errorTelefono = if (telefono.isNotEmpty() && !telefonoValido(telefono)) {
            ErrorPerfil.TELEFONO_INVALIDO
        } else null
        val errorComuna = if (estado.pideComuna && estado.comunaId == null) {
            ErrorPerfil.COMUNA_NO_ELEGIDA
        } else null

        val hayErrores = listOf(errorNombres, errorApellidos, errorCorreo, errorTelefono, errorComuna)
            .any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorCorreo = errorCorreo,
                    errorTelefono = errorTelefono,
                    errorComuna = errorComuna
                )
            }
            return null
        }

        return try {
            db.actualizarPerfil(
                CambiosPerfil(
                    id = actual.id,
                    nombres = nombres,
                    apellidos = apellidos,
                    correo = correo,
                    telefono = telefono.ifEmpty { null },
                    comunaId = if (estado.pideComuna) estado.comunaId else null
                )
            )
            db.buscarUsuarioPorId(actual.id) ?: fallo()
        } catch (e: Exception) {
            fallo(e)
        }
    }

    private fun telefonoValido(telefono: String): Boolean =
        telefono.all { it.isDigit() || it in "+ -" } && telefono.count { it.isDigit() } in 9..11

    private fun fallo(e: Exception? = null): Usuario? {
        if (e != null) Log.e("EditarPerfilViewModel", "Error al guardar el perfil", e)
        _uiState.update { it.copy(errorGeneral = ErrorPerfil.GENERICO) }
        return null
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}