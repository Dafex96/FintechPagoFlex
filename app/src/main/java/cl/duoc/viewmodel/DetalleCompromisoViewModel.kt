package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.DetalleCompromiso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetalleCompromisoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _detalle = MutableStateFlow<DetalleCompromiso?>(null)
    val detalle: StateFlow<DetalleCompromiso?> = _detalle.asStateFlow()

    fun cargar(idCompromiso: Long, idUsuario: Long) {
        _detalle.value = db.obtenerDetalleCompromiso(idCompromiso, idUsuario)
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}