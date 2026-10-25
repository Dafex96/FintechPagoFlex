package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.Pago
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistorialPagosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _pagos = MutableStateFlow<List<Pago>>(emptyList())
    val pagos: StateFlow<List<Pago>> = _pagos.asStateFlow()

    private val _pagoSeleccionado = MutableStateFlow<Pago?>(null)
    val pagoSeleccionado: StateFlow<Pago?> = _pagoSeleccionado.asStateFlow()

    fun cargarHistorial(idUsuario: Long) {
        val lista = db.listarPagosUsuario(idUsuario)
        _pagos.value = lista
    }

    fun seleccionarPago(pago: Pago?) {
        _pagoSeleccionado.value = pago
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
