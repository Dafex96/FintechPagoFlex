package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.Compromiso
import cl.duoc.model.EstadoCompromisoId
import cl.duoc.model.Pago
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HistorialUiState(
    val pagos: List<Pago> = emptyList(),
    val anulados: List<Compromiso> = emptyList(),
    val empresas: List<String> = emptyList(),
    val anios: List<Int> = emptyList(),
    val empresaFiltro: String? = null,
    val anioFiltro: Int? = null,
    val mesFiltro: Int? = null,
    val pagoSeleccionado: Pago? = null
)

class HistorialPagosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private var todosPagos: List<Pago> = emptyList()
    private var todosAnulados: List<Compromiso> = emptyList()

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    fun cargarHistorial(idUsuario: Long) {
        todosPagos = db.listarPagosUsuario(idUsuario)
        todosAnulados = db.listarCompromisosUsuario(idUsuario, EstadoCompromisoId.ANULADO.toLong())

        val empresas = (todosPagos.map { it.empresaNombre } + todosAnulados.map { it.empresaNombre })
            .distinct().sorted()
        val anios = (todosPagos.map { it.fechaHora.take(4).toIntOrNull() } +
                todosAnulados.map { it.fechaVencimiento.take(4).toIntOrNull() })
            .filterNotNull().distinct().sortedDescending()

        _uiState.update { it.copy(empresas = empresas, anios = anios) }
        aplicarFiltros()
    }

    fun filtrarEmpresa(empresa: String?) {
        _uiState.update { it.copy(empresaFiltro = empresa) }
        aplicarFiltros()
    }

    fun filtrarAnio(anio: Int?) {
        _uiState.update { it.copy(anioFiltro = anio) }
        aplicarFiltros()
    }

    fun filtrarMes(mes: Int?) {
        _uiState.update { it.copy(mesFiltro = mes) }
        aplicarFiltros()
    }

    fun seleccionarPago(pago: Pago?) {
        _uiState.update { it.copy(pagoSeleccionado = pago) }
    }

    private fun aplicarFiltros() {
        val f = _uiState.value
        fun coincide(empresa: String, fechaIso: String): Boolean {
            val anio = fechaIso.take(4).toIntOrNull()
            val mes = fechaIso.drop(5).take(2).toIntOrNull()
            return (f.empresaFiltro == null || empresa == f.empresaFiltro) &&
                    (f.anioFiltro == null || anio == f.anioFiltro) &&
                    (f.mesFiltro == null || mes == f.mesFiltro)
        }
        _uiState.update {
            it.copy(
                pagos = todosPagos.filter { p -> coincide(p.empresaNombre, p.fechaHora) },
                anulados = todosAnulados.filter { c -> coincide(c.empresaNombre, c.fechaVencimiento) }
            )
        }
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}