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

    fun programarPeriodico(context: Context) {
        val solicitud = PeriodicWorkRequestBuilder<AvisosWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            TRABAJO_PERIODICO,
            ExistingPeriodicWorkPolicy.KEEP,
            solicitud
        )
    }

    fun verificarAhora(context: Context) {
        val solicitud = OneTimeWorkRequestBuilder<AvisosWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            TRABAJO_INMEDIATO,
            ExistingWorkPolicy.REPLACE,
            solicitud
        )
    }
}
