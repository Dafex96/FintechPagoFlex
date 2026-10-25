package cl.duoc.notificaciones

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.duoc.data.PagoFlexDbHelper
import cl.duoc.data.SesionPreferencias
import cl.duoc.model.CanalId
import cl.duoc.model.EstadoCompromisoId
import cl.duoc.model.EstadoReporteId
import cl.duoc.model.Usuario
import cl.duoc.util.Fechas
import java.util.Calendar

/**
 * Revisa la sesión activa y envía los avisos que correspondan. Corre cada pocas horas
 * (AvisosScheduler) y también al entrar a la app, aunque la app no esté abierta.
 */
class AvisosWorker(context: Context, parametros: WorkerParameters) : CoroutineWorker(context, parametros) {

    private companion object {
        /** Cuántos días hacia atrás se considera "reciente" una novedad (pago, reporte, compromiso nuevo). */
        const val DIAS_NOVEDAD = 3
    }

    private val registro = RegistroAvisos(applicationContext)

    override suspend fun doWork(): Result {
        val contexto = applicationContext
        if (!NotificacionesHelper.puedeNotificar(contexto)) return Result.success()

        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (PoliticaAvisos.enHorarioSilencioso(hora)) return Result.success()

        val idUsuario = SesionPreferencias(contexto).leer()
        if (idUsuario < 0) return Result.success()

        val db = PagoFlexDbHelper(contexto)
        try {
            val usuario = db.buscarUsuarioPorId(idUsuario)?.takeIf { it.activo } ?: return Result.success()
            when {
                usuario.esUsuarioFinal -> avisarUsuarioFinal(db, usuario)
                usuario.esEjecutivo -> avisarEjecutivo(db, usuario)
            }
        } catch (e: Exception) {
            return Result.retry()
        } finally {
            db.close()
        }
        return Result.success()
    }

    private fun avisarUsuarioFinal(db: PagoFlexDbHelper, usuario: Usuario) {
        val preferencias = db.obtenerPreferenciasAvisos(usuario.id)
        if (!preferencias.activos) return

        val contexto = applicationContext
        val compromisos = db.listarCompromisosUsuario(usuario.id)

        // 1) Vencimientos: lo que más ayuda a la cobranza, va primero.
        for (compromiso in compromisos) {
            val dias = Fechas.diasHasta(compromiso.fechaVencimiento) ?: continue
            val tipo = PoliticaAvisos.tipoDeAviso(compromiso, preferencias.diasAnticipacion, dias) ?: continue
            enviarUnaVez("${usuario.id}:${tipo.name}:${compromiso.id}", compromiso.id) {
                NotificacionesHelper.avisoVencimiento(contexto, compromiso, tipo, dias)
            }
        }

        // 2) Pagos hechos por el portal o el agente conversacional (los de la app ya los ve en pantalla).
        db.listarPagosUsuario(usuario.id)
            .filter { it.idCanal != CanalId.APP.toLong() && esReciente(it.fechaHora) }
            .forEach { pago ->
                enviarUnaVez("${usuario.id}:pago:${pago.id}", pago.idCompromiso) {
                    NotificacionesHelper.pagoRegistrado(contexto, pago)
                }
            }

        // 3) Reportes que un agente ya resolvió.
        db.listarReportesUsuario(usuario.id)
            .filter { it.idEstadoReporte == EstadoReporteId.RESUELTO.toLong() && esReciente(it.fechaResolucion) }
            .forEach { reporte ->
                enviarUnaVez("${usuario.id}:reporte:${reporte.id}", reporte.idCompromiso) {
                    NotificacionesHelper.reporteResuelto(contexto, reporte)
                }
            }

        // 4) Compromisos que la empresa cargó hace poco.
        compromisos
            .filter { (it.esPendiente || it.esVencido) && esReciente(it.fechaRegistro) }
            .forEach { compromiso ->
                enviarUnaVez("${usuario.id}:nuevo:${compromiso.id}", compromiso.id) {
                    NotificacionesHelper.compromisoNuevo(contexto, compromiso)
                }
            }
    }

    private fun avisarEjecutivo(db: PagoFlexDbHelper, usuario: Usuario) {
        val idEmpresa = usuario.idEmpresa ?: return
        db.listarCompromisosEmpresa(idEmpresa, EstadoCompromisoId.EN_REVISION.toLong()).forEach { compromiso ->
            enviarUnaVez("${usuario.id}:revision:${compromiso.id}", compromiso.id) {
                NotificacionesHelper.compromisoEnRevision(applicationContext, compromiso)
            }
        }
    }

    private fun esReciente(fechaIso: String?): Boolean {
        val dias = fechaIso?.let { Fechas.diasHasta(it) } ?: return false
        return dias in -DIAS_NOVEDAD..0
    }

    private fun enviarUnaVez(clave: String, idCompromiso: Long, enviar: () -> Unit) {
        if (registro.yaEnviado(clave) || !registro.puedeAvisarHoy(idCompromiso)) return
        enviar()
        registro.registrar(clave, idCompromiso)
    }
}
