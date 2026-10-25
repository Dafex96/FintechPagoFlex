package cl.duoc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.data.EvaluadorReconocimientos
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.model.Reconocimiento
import cl.duoc.model.ReconocimientoId
import cl.duoc.model.ReconocimientoItem
import cl.duoc.model.TipoProgreso
import cl.duoc.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReconocimientosUiState(
    val cargando: Boolean = true,
    val hayError: Boolean = false,
    val obtenidos: List<ReconocimientoItem> = emptyList(),
    val porObtener: List<ReconocimientoItem> = emptyList(),
    val nuevos: List<Reconocimiento> = emptyList(),
    val detalle: ReconocimientoItem? = null
) {
    val total: Int get() = obtenidos.size + porObtener.size
}

class ReconocimientosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PagoFlexDbHelper(application)
    private val evaluador = EvaluadorReconocimientos(application, db)

    private val _uiState = MutableStateFlow(ReconocimientosUiState())
    val uiState: StateFlow<ReconocimientosUiState> = _uiState.asStateFlow()

    fun cargar(idUsuario: Long) {
        try {
            val resultado = evaluador.evaluar(idUsuario)
            val obtenidosPorId = db.listarReconocimientosUsuario(idUsuario).associateBy { it.idReconocimiento }

            val items = db.listarCatalogoReconocimientos().map { reconocimiento ->
                val obtenido = obtenidosPorId[reconocimiento.idReconocimiento]
                val (tipo, meta, actual) = progresoDe(reconocimiento.idReconocimiento, resultado)
                ReconocimientoItem(
                    id = reconocimiento.idReconocimiento,
                    nombre = reconocimiento.nombre,
                    descripcion = reconocimiento.descripcion,
                    obtenido = obtenido != null,
                    fechaObtencion = obtenido?.fechaObtencion?.let { Fechas.aFechaCorta(it) },
                    motivo = obtenido?.motivo,
                    tipoProgreso = tipo,
                    progresoActual = minOf(actual, meta),
                    progresoMeta = meta
                )
            }

            _uiState.update {
                it.copy(
                    cargando = false,
                    hayError = false,
                    obtenidos = items.filter { item -> item.obtenido }
                        .sortedByDescending { item -> obtenidosPorId[item.id]?.fechaObtencion },
                    porObtener = items.filter { item -> !item.obtenido },
                    nuevos = resultado.nuevos
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(cargando = false, hayError = true) }
        }
    }

    fun abrirDetalle(item: ReconocimientoItem) {
        _uiState.update { it.copy(detalle = item) }
    }

    fun cerrarDetalle() {
        _uiState.update { it.copy(detalle = null) }
    }

    fun cerrarCelebracion() {
        _uiState.update { it.copy(nuevos = emptyList()) }
    }

    private fun progresoDe(id: Int, resultado: EvaluadorReconocimientos.Resultado): Triple<TipoProgreso, Int, Int> =
        when (id) {
            ReconocimientoId.PERSONA_PUNTUAL ->
                Triple(TipoProgreso.PAGOS, EvaluadorReconocimientos.META_PAGOS_PUNTUAL, resultado.pagosATiempo)
            ReconocimientoId.TRES_MESES, ReconocimientoId.SEIS_MESES, ReconocimientoId.DOCE_MESES ->
                Triple(TipoProgreso.MESES, EvaluadorReconocimientos.METAS_MESES.getValue(id), resultado.mesesSeguidos)
            else -> Triple(TipoProgreso.NINGUNO, 0, 0)
        }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
