package cl.duoc.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.duoc.MainActivity
import cl.duoc.R
import cl.duoc.model.Compromiso

object NotificacionesHelper {

    private const val ID_CANAL = "pagoflex_avisos_vencimiento"
    private const val NOMBRE_CANAL = "Avisos de Vencimiento"
    private const val DESCRIPCION_CANAL = "Notificaciones para avisarte cuando un compromiso está por vencer o vencido."

    fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(ID_CANAL, NOMBRE_CANAL, importancia).apply {
                description = DESCRIPCION_CANAL
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    fun mostrarNotificacion(
        context: Context,
        titulo: String,
        mensaje: String,
        idNotificacion: Int = System.currentTimeMillis().toInt()
    ) {
        crearCanalNotificacion(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, ID_CANAL)
            .setSmallIcon(R.drawable.ic_check)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(idNotificacion, builder.build())
            } catch (e: SecurityException) {
            }
        }
    }

    fun notificarProximosVencimientos(
        context: Context,
        compromisos: List<Compromiso>,
        diasAnticipacion: Int
    ) {
        val pendientesOProximos = compromisos.filter { it.esPendiente || it.esVencido }
        if (pendientesOProximos.isEmpty()) return

        val vencidos = pendientesOProximos.filter { it.esVencido }
        if (vencidos.isNotEmpty()) {
            mostrarNotificacion(
                context = context,
                titulo = "¡Tienes compromisos vencidos!",
                mensaje = "Tienes ${vencidos.size} compromiso(s) por regularizar en PagoFlex.",
                idNotificacion = 2001
            )
        } else {
            val proximo = pendientesOProximos.firstOrNull()
            if (proximo != null) {
                mostrarNotificacion(
                    context = context,
                    titulo = "Aviso de vencimiento PagoFlex",
                    mensaje = "Tu compromiso '${proximo.concepto}' de ${proximo.empresaNombre} vence pronto (${proximo.fechaVencimiento}).",
                    idNotificacion = 2002
                )
            }
        }
    }

    fun enviarNotificacionPrueba(context: Context) {
        mostrarNotificacion(
            context = context,
            titulo = "🔔 Aviso de prueba PagoFlex",
            mensaje = "Las notificaciones locales están funcionando correctamente para los avisos de vencimiento.",
            idNotificacion = 3001
        )
    }
}
