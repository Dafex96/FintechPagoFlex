package cl.duoc.viewmodel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.ErrorRegistro
import cl.duoc.model.NuevoUsuario
import cl.duoc.model.RegistroUiState
import cl.duoc.model.Usuario
import cl.duoc.util.Rut
import cl.duoc.util.Seguridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _uiState = MutableStateFlow(RegistroUiState(comunas = db.listarComunas()))
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()


    fun onNombresCambia(valor: String) {
        _uiState.update { it.copy(nombres = valor, errorNombres = null, errorGeneral = null) }
    }

    fun onApellidosCambia(valor: String) {
        _uiState.update { it.copy(apellidos = valor, errorApellidos = null, errorGeneral = null) }
    }

    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor, errorRut = null, errorGeneral = null) }
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

    fun onContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(contrasena = valor, errorContrasena = null, errorConfirmar = null, errorGeneral = null)
        }
    }

    fun onConfirmarContrasenaCambia(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor, errorConfirmar = null, errorGeneral = null) }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onTerminosCambia(valor: Boolean) {
        _uiState.update { it.copy(aceptaTerminos = valor, errorTerminos = null, errorGeneral = null) }
    }

    fun onRegistrar(): Usuario? {
        val estado = _uiState.value

        val nombres = estado.nombres.trim()
        val apellidos = estado.apellidos.trim()
        val rutNormalizado = Rut.normalizar(estado.rut)
        val correo = estado.correo.trim()
        val telefono = estado.telefono.trim()

        val errorRut = try {
            when {
                estado.rut.isBlank() -> ErrorRegistro.CAMPO_VACIO
                !Rut.esValido(estado.rut) -> ErrorRegistro.RUT_INVALIDO
                db.existeRut(rutNormalizado) -> ErrorRegistro.RUT_EXISTE
                else -> null
            }
        } catch (e: Exception) {
            return falloInesperado(e)
        }

        val errorCorreo = try {
            when {
                correo.isBlank() -> ErrorRegistro.CAMPO_VACIO
                !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> ErrorRegistro.CORREO_INVALIDO
                db.existeCorreo(correo) -> ErrorRegistro.CORREO_EXISTE
                else -> null
            }
        } catch (e: Exception) {
            return falloInesperado(e)
        }

        val errorNombres = if (nombres.isBlank()) ErrorRegistro.CAMPO_VACIO else null
        val errorApellidos = if (apellidos.isBlank()) ErrorRegistro.CAMPO_VACIO else null
        val errorTelefono = if (telefono.isNotEmpty() && !telefonoValido(telefono)) {
            ErrorRegistro.TELEFONO_INVALIDO
        } else null
        val errorComuna = if (estado.comunaId == null) ErrorRegistro.COMUNA_NO_ELEGIDA else null
        val errorContrasena = when {
            estado.contrasena.isEmpty() -> ErrorRegistro.CAMPO_VACIO
            !contrasenaSegura(estado.contrasena) -> ErrorRegistro.CONTRASENA_DEBIL
            else -> null
        }
        val errorConfirmar = when {
            estado.confirmarContrasena.isEmpty() -> ErrorRegistro.CAMPO_VACIO
            estado.confirmarContrasena != estado.contrasena -> ErrorRegistro.CONTRASENAS_DISTINTAS
            else -> null
        }
        val errorTerminos = if (!estado.aceptaTerminos) ErrorRegistro.TERMINOS_NO_ACEPTADOS else null

        val hayErrores = listOf(
            errorNombres, errorApellidos, errorRut, errorCorreo, errorTelefono,
            errorComuna, errorContrasena, errorConfirmar, errorTerminos
        ).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorRut = errorRut,
                    errorCorreo = errorCorreo,
                    errorTelefono = errorTelefono,
                    errorComuna = errorComuna,
                    errorContrasena = errorContrasena,
                    errorConfirmar = errorConfirmar,
                    errorTerminos = errorTerminos
                )
            }
            return null
        }

        return try {
            val nuevo = NuevoUsuario(
                rut = rutNormalizado,
                nombres = nombres,
                apellidos = apellidos,
                correo = correo,
                telefono = telefono.ifEmpty { null },
                comunaId = estado.comunaId!!,
                contrasenaHash = Seguridad.hash(estado.contrasena)
            )
            val id = db.registrarUsuarioFinal(nuevo)
            val usuario = db.buscarUsuarioPorId(id)

            if (usuario == null) {
                _uiState.update { it.copy(errorGeneral = ErrorRegistro.GENERICO) }
                null
            } else {
                _uiState.update { it.copy(contrasena = "", confirmarContrasena = "") }
                usuario
            }
        } catch (e: Exception) {
            falloInesperado(e)
        }
    }

    private fun contrasenaSegura(contrasena: String): Boolean =
        contrasena.length >= 8 && contrasena.any { it.isLetter() } && contrasena.any { it.isDigit() }

    private fun telefonoValido(telefono: String): Boolean =
        telefono.all { it.isDigit() || it in "+ -" } && telefono.count { it.isDigit() } in 9..11

    private fun falloInesperado(e: Exception): Usuario? {
        Log.e("RegistroViewModel", "Error al registrar", e)
        _uiState.update { it.copy(errorGeneral = ErrorRegistro.GENERICO) }
        return null
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}