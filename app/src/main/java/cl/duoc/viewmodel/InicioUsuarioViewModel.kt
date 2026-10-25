package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.ResumenInicioUsuario
import cl.duoc.notificaciones.NotificacionesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InicioUsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _resumen = MutableStateFlow(ResumenInicioUsuario())
    val resumen: StateFlow<ResumenInicioUsuario> = _resumen.asStateFlow()

    fun cargarDatos(idUsuario: Long) {
        val resumenCargado = db.obtenerResumenInicioUsuario(idUsuario)
        _resumen.value = resumenCargado

        // Verificar e intentar notificar vencimientos locales
        val usuario = db.buscarUsuarioPorId(idUsuario)
        if (usuario != null && resumenCargado.compromisosPendientes.isNotEmpty()) {
            NotificacionesHelper.notificarProximosVencimientos(
                getApplication(),
                resumenCargado.compromisosPendientes + resumenCargado.compromisosVencidos,
                3
            )
        }
    }

    fun probarNotificacionLocal() {
        NotificacionesHelper.enviarNotificacionPrueba(getApplication())
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
