package cl.duoc.notificaciones

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object AvisosScheduler {

    private const val TRABAJO_PERIODICO = "pagoflex_avisos_periodico"
    private const val TRABAJO_INMEDIATO = "pagoflex_avisos_inmediato"

    /**
     * Revisión cada 6 horas: así siempre cae alguna dentro del horario permitido (08:00 a 22:00)
     * y los avisos salen aunque la persona no abra la app. Es seguro llamarlo en cada arranque.
     */
    fun programarPeriodico(context: Context) {
        val solicitud = PeriodicWorkRequestBuilder<AvisosWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            TRABAJO_PERIODICO,
            ExistingPeriodicWorkPolicy.KEEP,
            solicitud
        )
    }

    /** Una revisión ahora mismo: al abrir la app, o después de cambiar las preferencias de avisos. */
    fun verificarAhora(context: Context) {
        val solicitud = OneTimeWorkRequestBuilder<AvisosWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            TRABAJO_INMEDIATO,
            ExistingWorkPolicy.REPLACE,
            solicitud
        )
    }
}
