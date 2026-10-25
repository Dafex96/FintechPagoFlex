package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.Compromiso
import cl.duoc.model.NuevoCompromisoEmpresa
import cl.duoc.model.ResumenCobranza
import cl.duoc.notificaciones.AvisosScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EjecutivoUiState(
    val resumen: ResumenCobranza = ResumenCobranza(),
    val compromisos: List<Compromiso> = emptyList(),
    val filtroEstado: Long? = null,
    val compromisoParaAnular: Compromiso? = null,
    val motivoAnulacionSeleccionado: Long = 1,
    val comentarioAnulacion: String = "",
    val nuevoCompromiso: NuevoCompromisoEmpresa = NuevoCompromisoEmpresa(),
    val mensajeExito: String? = null,
    val errorRegistro: String? = null
)

class EjecutivoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)

    private val _uiState = MutableStateFlow(EjecutivoUiState())
    val uiState: StateFlow<EjecutivoUiState> = _uiState.asStateFlow()

    private var currentEmpresaId: Long = -1
    private var currentEjecutivoId: Long = -1

    fun cargarDatosEjecutivo(idEmpresa: Long?, idEjecutivo: Long) {
        if (idEmpresa == null) return
        currentEmpresaId = idEmpresa
        currentEjecutivoId = idEjecutivo

        // Avisa si algún compromiso de su empresa quedó En revisión por un reporte.
        AvisosScheduler.verificarAhora(getApplication())

        val resumen = db.obtenerResumenCobranzaEmpresa(idEmpresa)
        val compromisos = db.listarCompromisosEmpresa(idEmpresa, _uiState.value.filtroEstado)

        _uiState.update {
            it.copy(
                resumen = resumen,
                compromisos = compromisos
            )
        }
    }

    fun cambiarFiltroEstado(filtroEstado: Long?) {
        _uiState.update { it.copy(filtroEstado = filtroEstado) }
        if (currentEmpresaId != -1L) {
            val compromisos = db.listarCompromisosEmpresa(currentEmpresaId, filtroEstado)
            _uiState.update { it.copy(compromisos = compromisos) }
        }
    }

    fun seleccionarCompromisoParaAnular(compromiso: Compromiso?) {
        _uiState.update {
            it.copy(
                compromisoParaAnular = compromiso,
                motivoAnulacionSeleccionado = 1,
                comentarioAnulacion = ""
            )
        }
    }

    fun seleccionarMotivoAnulacion(idMotivo: Long) {
        _uiState.update { it.copy(motivoAnulacionSeleccionado = idMotivo) }
    }

    fun onComentarioAnulacionCambia(valor: String) {
        _uiState.update { it.copy(comentarioAnulacion = valor) }
    }

    fun confirmarAnulacion(): Boolean {
        val comp = _uiState.value.compromisoParaAnular ?: return false
        val motivo = _uiState.value.motivoAnulacionSeleccionado
        val comentario = _uiState.value.comentarioAnulacion

        val exito = db.anularCompromisoEmpresa(comp.id, currentEjecutivoId, motivo, comentario)
        if (exito) {
            _uiState.update {
                it.copy(
                    compromisoParaAnular = null,
                    mensajeExito = "Compromiso anulado correctamente."
                )
            }
            cargarDatosEjecutivo(currentEmpresaId, currentEjecutivoId)
            return true
        } else {
            _uiState.update { it.copy(errorRegistro = "No se pudo anular el compromiso (¿Está pagado?).") }
            return false
        }
    }

    fun actualizarNuevoCompromiso(cambios: (NuevoCompromisoEmpresa) -> NuevoCompromisoEmpresa) {
        _uiState.update { it.copy(nuevoCompromiso = cambios(it.nuevoCompromiso), errorRegistro = null) }
    }

    fun registrarCompromiso(): Boolean {
        val nuevo = _uiState.value.nuevoCompromiso
        if (nuevo.rutPersona.isBlank() || nuevo.concepto.isBlank() || nuevo.monto.isBlank() || nuevo.fechaVencimiento.isBlank()) {
            _uiState.update { it.copy(errorRegistro = "Completa los campos obligatorios (*).") }
            return false
        }

        try {
            val folio = db.registrarCompromisoEmpresa(currentEmpresaId, currentEjecutivoId, nuevo)
            _uiState.update {
                it.copy(
                    nuevoCompromiso = NuevoCompromisoEmpresa(),
                    mensajeExito = "Compromiso registrado con folio $folio",
                    errorRegistro = null
                )
            }
            cargarDatosEjecutivo(currentEmpresaId, currentEjecutivoId)
            return true
        } catch (e: Exception) {
            _uiState.update { it.copy(errorRegistro = "Error al registrar: ${e.localizedMessage}") }
            return false
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeExito = null, errorRegistro = null) }
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
