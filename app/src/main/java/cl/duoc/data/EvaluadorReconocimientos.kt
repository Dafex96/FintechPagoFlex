package cl.duoc.data

import android.content.Context
import cl.duoc.R
import cl.duoc.model.EstadoCompromisoId
import cl.duoc.model.Reconocimiento
import cl.duoc.model.ReconocimientoId
import cl.duoc.notificaciones.NotificacionesHelper
import cl.duoc.util.Fechas

/**
 * Decide qué reconocimientos le corresponden a una persona según hechos verificables de su
 * historial (RN-16): pagos a tiempo, meses seguidos al día y recomendaciones que se unieron.
 * Nunca mira el monto pagado y nunca quita un reconocimiento ya obtenido.
 */
class EvaluadorReconocimientos(
    private val context: Context,
    private val db: PagoFlexDbHelper
) {

    data class Resultado(
        val nuevos: List<Reconocimiento>,
        val pagosATiempo: Int,
        val mesesSeguidos: Int
    )

    companion object {
        const val META_PAGOS_PUNTUAL = 10
        val METAS_MESES = mapOf(
            ReconocimientoId.TRES_MESES to 3,
            ReconocimientoId.SEIS_MESES to 6,
            ReconocimientoId.DOCE_MESES to 12
        )
    }

    private data class PagoEvaluado(val fechaPago: String, val empresa: String, val aTiempo: Boolean, val mes: Int)

    /**
     * @param notificar avisa con una notificación cada reconocimiento nuevo (útil tras un pago,
     * innecesario cuando la persona ya está mirando la pantalla de reconocimientos).
     */
    fun evaluar(idUsuario: Long, notificar: Boolean = false): Resultado {
        val hoy = Fechas.hoyIso()
        val compromisos = db.listarCompromisosUsuario(idUsuario)
        val pagosPorCompromiso = db.listarPagosUsuario(idUsuario).associateBy { it.idCompromiso }
        val catalogo = db.listarCatalogoReconocimientos().associateBy { it.idReconocimiento }

        // Pagos ordenados por fecha, marcados a tiempo (fecha de pago <= fecha de vencimiento, RN-10).
        val pagos = compromisos.mapNotNull { compromiso ->
            val pago = pagosPorCompromiso[compromiso.id] ?: return@mapNotNull null
            PagoEvaluado(
                fechaPago = pago.fechaHora,
                empresa = compromiso.empresaNombre,
                aTiempo = pago.fechaHora.take(10) <= compromiso.fechaVencimiento.take(10),
                mes = indiceDeMes(compromiso.fechaVencimiento)
            )
        }.sortedBy { it.fechaPago }
        val aTiempo = pagos.filter { it.aTiempo }

        // Meses "al día": cerrados (nada vence hoy o después) y con todos sus compromisos pagados a tiempo.
        // Los anulados y los en revisión no cuentan ni a favor ni en contra, igual que en RN-10.
        val relevantes = compromisos.filter {
            it.idEstadoCompromiso != EstadoCompromisoId.ANULADO.toLong() &&
                    it.idEstadoCompromiso != EstadoCompromisoId.EN_REVISION.toLong()
        }
        val ultimoPagoPorMes = mutableMapOf<Int, String>()
        val mesesAlDia = relevantes.groupBy { indiceDeMes(it.fechaVencimiento) }
            .filter { (_, delMes) ->
                delMes.none { it.fechaVencimiento.take(10) >= hoy } &&
                        delMes.all { c ->
                            val pago = pagosPorCompromiso[c.id]
                            pago != null && pago.fechaHora.take(10) <= c.fechaVencimiento.take(10)
                        }
            }
            .keys.sorted()
        mesesAlDia.forEach { mes ->
            ultimoPagoPorMes[mes] = relevantes
                .filter { indiceDeMes(it.fechaVencimiento) == mes }
                .mapNotNull { pagosPorCompromiso[it.id]?.fechaHora }
                .max()
        }

        // Racha de meses consecutivos; se guarda en qué fecha se alcanzó cada meta.
        var racha = 0
        var mejorRacha = 0
        var mesAnterior = Int.MIN_VALUE
        val fechaMeta = mutableMapOf<Int, String>()
        for (mes in mesesAlDia) {
            racha = if (mes == mesAnterior + 1) racha + 1 else 1
            mesAnterior = mes
            mejorRacha = maxOf(mejorRacha, racha)
            if (racha in METAS_MESES.values && racha !in fechaMeta) {
                fechaMeta[racha] = ultimoPagoPorMes.getValue(mes)
            }
        }

        val nuevos = mutableListOf<Reconocimiento>()
        fun otorgar(id: Int, fecha: String, motivo: String) {
            if (db.otorgarReconocimiento(idUsuario, id, fecha, motivo)) {
                catalogo[id]?.let { nuevos.add(it) }
            }
        }

        aTiempo.firstOrNull()?.let {
            otorgar(
                ReconocimientoId.PRIMER_PAGO, it.fechaPago,
                context.getString(R.string.recon_motivo_primer_pago, it.empresa)
            )
        }
        if (aTiempo.size >= META_PAGOS_PUNTUAL) {
            otorgar(
                ReconocimientoId.PERSONA_PUNTUAL, aTiempo[META_PAGOS_PUNTUAL - 1].fechaPago,
                context.getString(R.string.recon_motivo_pagos, META_PAGOS_PUNTUAL)
            )
        }
        METAS_MESES.forEach { (id, meses) ->
            fechaMeta[meses]?.let { fecha ->
                otorgar(id, fecha, context.resources.getQuantityString(R.plurals.recon_motivo_meses, meses, meses))
            }
        }
        db.listarRecomendacionesQueSeUnieron(idUsuario).firstOrNull()?.let { (organizacion, fecha) ->
            otorgar(
                ReconocimientoId.IMPULSOR, fecha,
                context.getString(R.string.recon_motivo_recomendacion, organizacion)
            )
        }

        if (notificar && nuevos.isNotEmpty() && db.obtenerPreferenciasAvisos(idUsuario).activos) {
            nuevos.forEach { NotificacionesHelper.reconocimientoNuevo(context, it.nombre) }
        }

        return Resultado(nuevos, aTiempo.size, mejorRacha)
    }

    private fun indiceDeMes(fechaIso: String): Int {
        val partes = fechaIso.take(10).split("-")
        return partes[0].toInt() * 12 + (partes[1].toInt() - 1)
    }
}
