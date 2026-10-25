package cl.duoc.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import cl.duoc.R
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.AvisoAnticipacion
import cl.duoc.notificaciones.AvisosScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AvisosUiState(
    val activos: Boolean = true,
    val diasAnticipacion: Int = AvisoAnticipacion.POR_DEFECTO,
    @StringRes val mensaje: Int? = null
)

class AvisosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _uiState = MutableStateFlow(AvisosUiState())
    val uiState: StateFlow<AvisosUiState> = _uiState.asStateFlow()

    private var idUsuario: Long = -1

    fun cargar(idUsuario: Long) {
        this.idUsuario = idUsuario
        val preferencias = db.obtenerPreferenciasAvisos(idUsuario)
        _uiState.update {
            it.copy(activos = preferencias.activos, diasAnticipacion = preferencias.diasAnticipacion)
        }
    }

    fun cambiarActivos(activos: Boolean) = guardar(activos, _uiState.value.diasAnticipacion)

    fun cambiarAnticipacion(dias: Int) = guardar(_uiState.value.activos, dias)

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    private fun guardar(activos: Boolean, dias: Int) {
        if (idUsuario < 0 || dias !in AvisoAnticipacion.OPCIONES) return
        val guardado = db.actualizarAvisosUsuario(idUsuario, activos, dias)
        if (guardado) {
            _uiState.update {
                it.copy(activos = activos, diasAnticipacion = dias, mensaje = R.string.avisos_guardado)
            }
            // Con las preferencias nuevas puede haber un aviso pendiente: se revisa de inmediato.
            if (activos) AvisosScheduler.verificarAhora(getApplication())
        } else {
            _uiState.update { it.copy(mensaje = R.string.avisos_error_guardar) }
        }
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
