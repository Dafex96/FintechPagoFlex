package cl.duoc.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Fechas {

    private const val FORMATO_FECHA = "yyyy-MM-dd"
    private const val FORMATO_FECHA_HORA = "yyyy-MM-dd'T'HH:mm:ss"
    private const val MILIS_POR_DIA = 86_400_000.0

    /**
     * Fecha fija de los datos de prueba (Anexo 9: 06-10-2026). Con ella los días de atraso, los vencimientos y el
     * indicador de cumplimiento coinciden con el anexo sin importar cuándo se abra la app. Poner null para usar el
     * reloj real del teléfono. La hora del día siempre es la real.
     */
    private val FECHA_DEMO: String? = "2026-10-06"

    /** Calendario "de hoy" según el reloj de la app (fecha de demostración o real). */
    fun calendarioActual(): Calendar {
        val calendario = Calendar.getInstance()
        val fija = FECHA_DEMO ?: return calendario
        val partes = fija.split("-")
        calendario.set(partes[0].toInt(), partes[1].toInt() - 1, partes[2].toInt())
        return calendario
    }

    fun hoyIso(): String = SimpleDateFormat(FORMATO_FECHA, Locale.US).format(calendarioActual().time)

    fun ahoraIso(): String = SimpleDateFormat(FORMATO_FECHA_HORA, Locale.US).format(calendarioActual().time)

    fun aFechaCorta(iso: String): String {
        val partes = iso.take(10).split("-")
        return if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else iso
    }

    /** Días de calendario entre hoy y la fecha dada (negativo si ya pasó). Null si la fecha no es válida. */
    fun diasHasta(iso: String, ahora: Calendar = calendarioActual()): Int? {
        val fecha = try {
            SimpleDateFormat(FORMATO_FECHA, Locale.US).apply { isLenient = false }.parse(iso.take(10))
        } catch (e: ParseException) {
            null
        } ?: return null

        val inicioHoy = (ahora.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return Math.round((fecha.time - inicioHoy.timeInMillis) / MILIS_POR_DIA).toInt()
    }

    /** Fecha corta (DD-MM-AAAA) de hoy más [dias] días. */
    fun fechaCortaEnDias(dias: Int): String {
        val calendario = calendarioActual().apply { add(Calendar.DAY_OF_YEAR, dias) }
        return SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendario.time)
    }
}
