package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.ResumenInicioUsuario
import cl.duoc.notificaciones.AvisosScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InicioUsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _resumen = MutableStateFlow(ResumenInicioUsuario())
    val resumen: StateFlow<ResumenInicioUsuario> = _resumen.asStateFlow()

    private val _avisosActivos = MutableStateFlow(true)
    val avisosActivos: StateFlow<Boolean> = _avisosActivos.asStateFlow()

    fun cargarDatos(idUsuario: Long) {
        _resumen.value = db.obtenerResumenInicioUsuario(idUsuario)
        _avisosActivos.value = db.obtenerPreferenciasAvisos(idUsuario).activos

        // Revisa vencimientos con las preferencias de la persona (anticipación, horario, un aviso por día).
        AvisosScheduler.verificarAhora(getApplication())
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
