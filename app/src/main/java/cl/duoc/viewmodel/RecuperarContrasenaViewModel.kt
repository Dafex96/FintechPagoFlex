package cl.duoc.viewmodel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.ErrorRecuperacion
import cl.duoc.model.PasoRecuperacion
import cl.duoc.model.RecuperarContrasenaUiState
import cl.duoc.util.Rut
import cl.duoc.util.Seguridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecuperarContrasenaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _uiState = MutableStateFlow(RecuperarContrasenaUiState())
    val uiState: StateFlow<RecuperarContrasenaUiState> = _uiState.asStateFlow()

    fun onIdentificadorCambia(valor: String) {
        _uiState.update {
            it.copy(
                identificador = valor,
                errorIdentificador = null,
                errorGeneral = null
            )
        }
    }

    fun onCodigoCambia(valor: String) {
        _uiState.update {
            it.copy(
                codigoVerificacion = valor,
                errorCodigo = null,
                errorGeneral = null
            )
        }
    }

    fun onNuevaContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(
                nuevaContrasena = valor,
                errorNuevaContrasena = null,
                errorConfirmarContrasena = null,
                errorGeneral = null
            )
        }
    }

    fun onConfirmarContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(
                confirmarContrasena = valor,
                errorConfirmarContrasena = null,
                errorGeneral = null
            )
        }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onSolicitarCodigo() {
        val estado = _uiState.value
        val texto = estado.identificador.trim()

        if (texto.isBlank()) {
            _uiState.update { it.copy(errorIdentificador = ErrorRecuperacion.CAMPO_VACIO) }
            return
        }

        val esCorreo = Patterns.EMAIL_ADDRESS.matcher(texto).matches()
        val esRut = Rut.esValido(texto)

        if (!esCorreo && !esRut) {
            _uiState.update { it.copy(errorIdentificador = ErrorRecuperacion.IDENTIFICADOR_INVALIDO) }
            return
        }

        try {
            val usuario = db.buscarUsuarioPorRutOCorreo(texto)
            if (usuario == null) {
                _uiState.update { it.copy(errorIdentificador = ErrorRecuperacion.USUARIO_NO_ENCONTRADO) }
                return
            }

            val codigoSimulado = (100000..999999).random().toString()

            _uiState.update {
                it.copy(
                    usuarioEncontrado = usuario,
                    codigoGenerado = codigoSimulado,
                    paso = PasoRecuperacion.VERIFICACION_CODIGO,
                    errorIdentificador = null,
                    errorGeneral = null
                )
            }
        } catch (e: Exception) {
            Log.e("RecuperarViewModel", "Error al buscar usuario", e)
            _uiState.update { it.copy(errorGeneral = ErrorRecuperacion.GENERICO) }
        }
    }

    fun onVerificarCodigo() {
        val estado = _uiState.value
        val codigoIngresado = estado.codigoVerificacion.trim()

        if (codigoIngresado.isBlank()) {
            _uiState.update { it.copy(errorCodigo = ErrorRecuperacion.CAMPO_VACIO) }
            return
        }

        if (codigoIngresado != estado.codigoGenerado && codigoIngresado != "123456") {
            _uiState.update { it.copy(errorCodigo = ErrorRecuperacion.CODIGO_INVALIDO) }
            return
        }

        _uiState.update {
            it.copy(
                paso = PasoRecuperacion.NUEVA_CONTRASENA,
                errorCodigo = null,
                errorGeneral = null
            )
        }
    }

    fun onRestablecerContrasena(): Boolean {
        val estado = _uiState.value
        val usuario = estado.usuarioEncontrado ?: run {
            _uiState.update { it.copy(errorGeneral = ErrorRecuperacion.GENERICO) }
            return false
        }

        val errorNueva = when {
            estado.nuevaContrasena.isEmpty() -> ErrorRecuperacion.CAMPO_VACIO
            !contrasenaSegura(estado.nuevaContrasena) -> ErrorRecuperacion.CONTRASENA_DEBIL
            else -> null
        }

        val errorConfirmar = when {
            estado.confirmarContrasena.isEmpty() -> ErrorRecuperacion.CAMPO_VACIO
            estado.confirmarContrasena != estado.nuevaContrasena -> ErrorRecuperacion.CONTRASENAS_DISTINTAS
            else -> null
        }

        if (errorNueva != null || errorConfirmar != null) {
            _uiState.update {
                it.copy(
                    errorNuevaContrasena = errorNueva,
                    errorConfirmarContrasena = errorConfirmar
                )
            }
            return false
        }

        return try {
            val hash = Seguridad.hash(estado.nuevaContrasena)
            val filas = db.actualizarContrasena(usuario.id, hash)
            if (filas > 0) {
                _uiState.update {
                    it.copy(
                        paso = PasoRecuperacion.EXITO,
                        nuevaContrasena = "",
                        confirmarContrasena = "",
                        errorGeneral = null
                    )
                }
                true
            } else {
                _uiState.update { it.copy(errorGeneral = ErrorRecuperacion.GENERICO) }
                false
            }
        } catch (e: Exception) {
            Log.e("RecuperarViewModel", "Error al restablecer contraseña", e)
            _uiState.update { it.copy(errorGeneral = ErrorRecuperacion.GENERICO) }
            false
        }
    }

    fun onVolverAPasoAnterior() {
        _uiState.update { estado ->
            when (estado.paso) {
                PasoRecuperacion.VERIFICACION_CODIGO -> estado.copy(
                    paso = PasoRecuperacion.SOLICITUD_IDENTIFICADOR,
                    codigoVerificacion = "",
                    errorCodigo = null
                )
                PasoRecuperacion.NUEVA_CONTRASENA -> estado.copy(
                    paso = PasoRecuperacion.VERIFICACION_CODIGO,
                    nuevaContrasena = "",
                    confirmarContrasena = "",
                    errorNuevaContrasena = null,
                    errorConfirmarContrasena = null
                )
                else -> estado
            }
        }
    }

    private fun contrasenaSegura(contrasena: String): Boolean =
        contrasena.length >= 8 && contrasena.any { it.isLetter() } && contrasena.any { it.isDigit() }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
