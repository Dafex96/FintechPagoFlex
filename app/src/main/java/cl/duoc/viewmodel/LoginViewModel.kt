package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.DatosIniciales
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.CuentaDemo
import cl.duoc.model.ErrorLogin
import cl.duoc.model.LoginUiState
import cl.duoc.model.Usuario
import cl.duoc.util.Rut
import cl.duoc.util.Seguridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor, errorRut = null, errorGeneral = null) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null) }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onUsarCuentaDemo(cuenta: CuentaDemo) {
        _uiState.update {
            it.copy(
                rut = cuenta.rut,
                contrasena = DatosIniciales.CONTRASENA_DEMO,
                errorRut = null,
                errorContrasena = null,
                errorGeneral = null
            )
        }
    }

    fun onIngresar(): Usuario? {
        val estado = _uiState.value

        // 1) Validar lo escrito (sin tocar la base de datos)
        val errorRut = when {
            estado.rut.isBlank() -> ErrorLogin.CAMPO_VACIO
            !Rut.esValido(estado.rut) -> ErrorLogin.RUT_INVALIDO
            else -> null
        }
        val errorContrasena = if (estado.contrasena.isEmpty()) ErrorLogin.CAMPO_VACIO else null

        if (errorRut != null || errorContrasena != null) {
            _uiState.update { it.copy(errorRut = errorRut, errorContrasena = errorContrasena) }
            return null
        }

        val usuario = try {
            db.validarCredenciales(Rut.normalizar(estado.rut), Seguridad.hash(estado.contrasena))
        } catch (e: Exception) {
            _uiState.update { it.copy(errorGeneral = ErrorLogin.GENERICO) }
            return null
        }

        return when {
            usuario == null -> {
                _uiState.update { it.copy(errorGeneral = ErrorLogin.CREDENCIALES) }
                null
            }
            !usuario.activo -> {
                _uiState.update { it.copy(errorGeneral = ErrorLogin.INACTIVO) }
                null
            }
            else -> {
                _uiState.update { it.copy(contrasena = "") }
                usuario
            }
        }
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}