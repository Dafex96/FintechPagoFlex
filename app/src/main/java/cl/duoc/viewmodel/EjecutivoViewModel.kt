package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.R
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.Compromiso
import cl.duoc.model.MotivoAnulacionId
import cl.duoc.model.NuevoCompromisoEmpresa
import cl.duoc.model.Reporte
import cl.duoc.model.ResumenCobranza
import cl.duoc.notificaciones.AvisosScheduler
import cl.duoc.util.Fechas
import cl.duoc.util.Rut
import cl.duoc.util.ValidadorCompromiso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EjecutivoUiState(
    val resumen: ResumenCobranza = ResumenCobranza(),
    val mes: String = Fechas.hoyIso().take(7),
    val compromisos: List<Compromiso> = emptyList(),
    val reportes: Map<Long, Reporte> = emptyMap(),
    val filtroEstado: Long? = null,
    val filtroMes: String? = null,
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

    private fun texto(id: Int, vararg args: Any): String = getApplication<Application>().getString(id, *args)

    fun cargarDatosEjecutivo(idEmpresa: Long?, idEjecutivo: Long) {
        if (idEmpresa == null) return
        currentEmpresaId = idEmpresa
        currentEjecutivoId = idEjecutivo

        AvisosScheduler.verificarAhora(getApplication())

        val resumen = db.obtenerResumenCobranzaEmpresa(idEmpresa, _uiState.value.mes)
        _uiState.update { it.copy(resumen = resumen) }
        recargarLista()
    }

    private fun recargarLista() {
        if (currentEmpresaId == -1L) return
        val estado = _uiState.value
        val compromisos = db.listarCompromisosEmpresa(currentEmpresaId, estado.filtroEstado, estado.filtroMes)
        val reportes = compromisos
            .filter { it.esEnRevision }
            .mapNotNull { c -> db.obtenerReportePorCompromiso(c.id)?.let { c.id to it } }
            .toMap()
        _uiState.update { it.copy(compromisos = compromisos, reportes = reportes) }
    }

    fun cambiarMes(mes: String) {
        _uiState.update { it.copy(mes = mes) }
        if (currentEmpresaId != -1L) {
            val resumen = db.obtenerResumenCobranzaEmpresa(currentEmpresaId, mes)
            _uiState.update { it.copy(resumen = resumen) }
        }
    }

    fun cambiarFiltroEstado(filtroEstado: Long?) {
        _uiState.update { it.copy(filtroEstado = filtroEstado) }
        recargarLista()
    }

    fun cambiarFiltroMes(mes: String?) {
        _uiState.update { it.copy(filtroMes = mes) }
        recargarLista()
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
        val comentario = _uiState.value.comentarioAnulacion.trim()

        if (motivo == MotivoAnulacionId.OTRO.toLong() && comentario.isBlank()) {
            _uiState.update { it.copy(errorRegistro = texto(R.string.error_anulacion_comentario)) }
            return false
        }
        val exito = db.anularCompromisoEmpresa(comp.id, currentEjecutivoId, currentEmpresaId, motivo, comentario)
        return if (exito) {
            _uiState.update {
                it.copy(compromisoParaAnular = null, mensajeExito = texto(R.string.anulacion_exitosa))
            }
            cargarDatosEjecutivo(currentEmpresaId, currentEjecutivoId)
            true
        } else {
            _uiState.update { it.copy(errorRegistro = texto(R.string.error_no_se_pudo_anular)) }
            false
        }
    }

    fun actualizarNuevoCompromiso(cambios: (NuevoCompromisoEmpresa) -> NuevoCompromisoEmpresa) {
        _uiState.update { it.copy(nuevoCompromiso = cambios(it.nuevoCompromiso), errorRegistro = null) }
    }

    fun registrarCompromiso(): Boolean {
        val nuevo = _uiState.value.nuevoCompromiso
        val rut = Rut.normalizar(nuevo.rutPersona)

        ValidadorCompromiso.validar(nuevo, personaExiste = db.existeRut(rut))?.let { mensaje ->
            _uiState.update { it.copy(errorRegistro = texto(mensaje)) }
            return false
        }

        val cuota = nuevo.numeroCuota.trim().toIntOrNull()
        if (db.existeCompromisoDuplicado(currentEmpresaId, rut, nuevo.concepto.trim(), cuota)) {
            _uiState.update { it.copy(errorRegistro = texto(R.string.error_compromiso_duplicado)) }
            return false
        }

        return try {
            val datos = nuevo.copy(fechaVencimiento = Fechas.deFechaCortaAIso(nuevo.fechaVencimiento)!!)
            val folio = db.registrarCompromisoEmpresa(currentEmpresaId, currentEjecutivoId, datos)
            _uiState.update {
                it.copy(
                    nuevoCompromiso = NuevoCompromisoEmpresa(),
                    mensajeExito = texto(R.string.registro_exitoso, folio),
                    errorRegistro = null
                )
            }
            cargarDatosEjecutivo(currentEmpresaId, currentEjecutivoId)
            true
        } catch (e: Exception) {
            _uiState.update { it.copy(errorRegistro = texto(R.string.error_no_se_pudo_registrar)) }
            false
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