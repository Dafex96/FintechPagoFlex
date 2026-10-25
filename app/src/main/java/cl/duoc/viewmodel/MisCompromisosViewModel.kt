package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.EvaluadorReconocimientos
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.Compromiso
import cl.duoc.model.Pago
import cl.duoc.model.Reporte
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MisCompromisosUiState(
    val compromisos: List<Compromiso> = emptyList(),
    val filtroEstado: Long? = null,
    val compromisoParaPagar: Compromiso? = null,
    val compromisoParaReportar: Compromiso? = null,
    val pagoExitoso: Pago? = null,
    val reporteExitoso: Reporte? = null,
    val medioPagoSeleccionado: Long = 1, // 1: Debito, 2: Credito, 3: Transferencia, 4: Simulado
    val motivoReporteSeleccionado: Long = 1,
    val comentarioReporte: String = "",
    val cargando: Boolean = false
)

class MisCompromisosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _uiState = MutableStateFlow(MisCompromisosUiState())
    val uiState: StateFlow<MisCompromisosUiState> = _uiState.asStateFlow()

    private var currentUsuarioId: Long = -1

    fun cargarCompromisos(idUsuario: Long) {
        currentUsuarioId = idUsuario
        val lista = db.listarCompromisosUsuario(idUsuario, _uiState.value.filtroEstado)
        _uiState.update { it.copy(compromisos = lista) }
    }

    fun cambiarFiltro(filtroEstado: Long?) {
        _uiState.update { it.copy(filtroEstado = filtroEstado) }
        if (currentUsuarioId != -1L) {
            cargarCompromisos(currentUsuarioId)
        }
    }

    fun seleccionarCompromisoParaPagar(compromiso: Compromiso?) {
        _uiState.update { it.copy(compromisoParaPagar = compromiso, pagoExitoso = null) }
    }

    fun seleccionarMedioPago(idMedioPago: Long) {
        _uiState.update { it.copy(medioPagoSeleccionado = idMedioPago) }
    }

    fun realizarPagoSimulado() {
        val comp = _uiState.value.compromisoParaPagar ?: return
        val medio = _uiState.value.medioPagoSeleccionado
        val pago = db.registrarPago(comp.id, medio)
        // Un pago a tiempo puede desbloquear un reconocimiento; si falla, el pago igual quedó registrado.
        runCatching {
            EvaluadorReconocimientos(getApplication(), db).evaluar(currentUsuarioId, notificar = true)
        }
        _uiState.update {
            it.copy(
                compromisoParaPagar = null,
                pagoExitoso = pago
            )
        }
        cargarCompromisos(currentUsuarioId)
    }

    fun seleccionarCompromisoParaReportar(compromiso: Compromiso?) {
        _uiState.update {
            it.copy(
                compromisoParaReportar = compromiso,
                reporteExitoso = null,
                comentarioReporte = ""
            )
        }
    }

    fun seleccionarMotivoReporte(idMotivo: Long) {
        _uiState.update { it.copy(motivoReporteSeleccionado = idMotivo) }
    }

    fun onComentarioReporteCambia(valor: String) {
        _uiState.update { it.copy(comentarioReporte = valor) }
    }

    fun enviarReporte() {
        val comp = _uiState.value.compromisoParaReportar ?: return
        val motivo = _uiState.value.motivoReporteSeleccionado
        val comentario = _uiState.value.comentarioReporte
        val reporte = db.registrarReporte(comp.id, motivo, comentario)
        _uiState.update {
            it.copy(
                compromisoParaReportar = null,
                reporteExitoso = reporte
            )
        }
        cargarCompromisos(currentUsuarioId)
    }

    fun cerrarModalExito() {
        _uiState.update { it.copy(pagoExitoso = null, reporteExitoso = null) }
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
