package cl.duoc.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cl.duoc.MainActivity
import cl.duoc.R
import cl.duoc.model.Compromiso
import cl.duoc.model.Pago
import cl.duoc.model.Reporte
import cl.duoc.navigation.Ruta
import cl.duoc.util.Fechas
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

/**
 * Único punto de la app que habla con el sistema de notificaciones de Android.
 * Los textos viven en strings_avisos.xml y mantienen el tono positivo de RN-11.
 */
object NotificacionesHelper {

    const val EXTRA_DESTINO = "cl.duoc.notificaciones.EXTRA_DESTINO"

    private const val CANAL_AVISOS = "pagoflex_avisos"
    private const val CANAL_GESTION = "pagoflex_gestion"
    private const val CANAL_TRABAJO = "pagoflex_trabajo"
    private const val CANAL_ANTIGUO = "pagoflex_avisos_vencimiento"

    /** Se puede llamar muchas veces: Android ignora los canales que ya existen. */
    fun crearCanales(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        // El canal de la versión anterior se creó con importancia media y Android no permite subirla después.
        manager.deleteNotificationChannel(CANAL_ANTIGUO)

        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CANAL_AVISOS,
                    context.getString(R.string.notif_canal_avisos_nombre),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = context.getString(R.string.notif_canal_avisos_desc) },
                NotificationChannel(
                    CANAL_GESTION,
                    context.getString(R.string.notif_canal_gestion_nombre),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = context.getString(R.string.notif_canal_gestion_desc) },
                NotificationChannel(
                    CANAL_TRABAJO,
                    context.getString(R.string.notif_canal_trabajo_nombre),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = context.getString(R.string.notif_canal_trabajo_desc) }
            )
        )
    }

    /** Falso si falta el permiso (Android 13+) o si la persona silenció la app desde los ajustes. */
    fun puedeNotificar(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    // ---- Usuario final -------------------------------------------------------------------------

    fun avisoVencimiento(context: Context, compromiso: Compromiso, tipo: PoliticaAvisos.TipoAviso, diasParaVencer: Int) {
        val fecha = Fechas.aFechaCorta(compromiso.fechaVencimiento)
        val empresa = compromiso.empresaNombre
        when (tipo) {
            PoliticaAvisos.TipoAviso.PREVIO -> publicar(
                context, CANAL_AVISOS, "previo:${compromiso.id}",
                titulo = context.getString(R.string.notif_vence_titulo),
                texto = context.resources.getQuantityString(
                    R.plurals.notif_vence_texto, diasParaVencer, diasParaVencer, empresa, fecha
                ),
                prioridad = NotificationCompat.PRIORITY_HIGH,
                destino = Ruta.Compromisos.ruta,
                conAccionPagar = true
            )
            PoliticaAvisos.TipoAviso.HOY -> publicar(
                context, CANAL_AVISOS, "hoy:${compromiso.id}",
                titulo = context.getString(R.string.notif_vence_hoy_titulo),
                texto = context.getString(R.string.notif_vence_hoy_texto, empresa, fecha),
                prioridad = NotificationCompat.PRIORITY_HIGH,
                destino = Ruta.Compromisos.ruta,
                conAccionPagar = true
            )
            PoliticaAvisos.TipoAviso.VENCIDO -> publicar(
                context, CANAL_AVISOS, "vencido:${compromiso.id}",
                titulo = context.getString(R.string.notif_vencido_titulo),
                texto = context.getString(R.string.notif_vencido_texto, empresa),
                prioridad = NotificationCompat.PRIORITY_DEFAULT,
                destino = Ruta.Compromisos.ruta,
                conAccionPagar = true
            )
        }
    }

    fun pagoRegistrado(context: Context, pago: Pago) {
        publicar(
            context, CANAL_GESTION, "pago:${pago.id}",
            titulo = context.getString(R.string.notif_pago_titulo),
            texto = context.getString(R.string.notif_pago_texto, formatoMoneda(pago.montoPagado), pago.empresaNombre),
            prioridad = NotificationCompat.PRIORITY_DEFAULT,
            destino = Ruta.HistorialPagos.ruta
        )
    }

    fun reporteResuelto(context: Context, reporte: Reporte) {
        publicar(
            context, CANAL_GESTION, "reporte:${reporte.id}",
            titulo = context.getString(R.string.notif_reporte_resuelto_titulo),
            texto = context.getString(R.string.notif_reporte_resuelto_texto, reporte.numero),
            prioridad = NotificationCompat.PRIORITY_DEFAULT,
            destino = Ruta.Compromisos.ruta
        )
    }

    fun compromisoNuevo(context: Context, compromiso: Compromiso) {
        publicar(
            context, CANAL_GESTION, "nuevo:${compromiso.id}",
            titulo = context.getString(R.string.notif_compromiso_nuevo_titulo),
            texto = context.getString(
                R.string.notif_compromiso_nuevo_texto,
                compromiso.empresaNombre,
                formatoMoneda(compromiso.monto),
                Fechas.aFechaCorta(compromiso.fechaVencimiento)
            ),
            prioridad = NotificationCompat.PRIORITY_LOW,
            destino = Ruta.Compromisos.ruta
        )
    }

    fun reconocimientoNuevo(context: Context, nombre: String) {
        publicar(
            context, CANAL_GESTION, "reconocimiento:$nombre",
            titulo = context.getString(R.string.notif_recon_titulo),
            texto = context.getString(R.string.notif_recon_texto, nombre),
            prioridad = NotificationCompat.PRIORITY_LOW,
            destino = Ruta.Reconocimientos.ruta
        )
    }

    // ---- Ejecutivo de empresa cliente ----------------------------------------------------------

    fun compromisoEnRevision(context: Context, compromiso: Compromiso) {
        publicar(
            context, CANAL_TRABAJO, "revision:${compromiso.id}",
            titulo = context.getString(R.string.notif_ejecutivo_revision_titulo),
            texto = context.getString(R.string.notif_ejecutivo_revision_texto, compromiso.folio),
            prioridad = NotificationCompat.PRIORITY_DEFAULT,
            destino = Ruta.EjecutivoCompromisos.ruta
        )
    }

    // ---- Internos ------------------------------------------------------------------------------

    private fun publicar(
        context: Context,
        canal: String,
        clave: String,
        titulo: String,
        texto: String,
        prioridad: Int,
        destino: String,
        conAccionPagar: Boolean = false
    ) {
        if (!puedeNotificar(context)) return
        crearCanales(context)

        val id = abs(clave.hashCode())
        val builder = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(prioridad)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(intentHacia(context, id, destino))
            .setAutoCancel(true)

        if (conAccionPagar) {
            builder.addAction(0, context.getString(R.string.notif_accion_pagar), intentHacia(context, id + 1, destino))
        }

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (e: SecurityException) {
            // El permiso se revocó justo ahora: no hay nada más que hacer.
        }
    }

    private fun intentHacia(context: Context, codigo: Int, destino: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINO, destino)
        }
        return PendingIntent.getActivity(
            context, codigo, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun formatoMoneda(monto: Int): String =
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(monto)
}
