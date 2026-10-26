package cl.duoc.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Fechas {

    private const val FORMATO_FECHA = "yyyy-MM-dd"
    private const val FORMATO_FECHA_HORA = "yyyy-MM-dd'T'HH:mm:ss"
    private const val MILIS_POR_DIA = 86_400_000.0

    private val FECHA_DEMO: String? = "2026-10-06"

    private val NOMBRES_MES = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    fun nombreMes(mes: Int): String = NOMBRES_MES.getOrElse(mes - 1) { "" }

    fun etiquetaMes(anioMes: String): String {
        val partes = anioMes.split("-")
        return if (partes.size == 2) "${nombreMes(partes[1].toIntOrNull() ?: 0)} ${partes[0]}" else anioMes
    }

    fun mesesAlrededor(atras: Int, adelante: Int): List<String> {
        val formato = SimpleDateFormat("yyyy-MM", Locale.US)
        return (-atras..adelante).map { d ->
            val cal = calendarioActual().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, d)
            }
            formato.format(cal.time)
        }
    }

    fun haceMesesIso(meses: Int): String {
        val cal = calendarioActual().apply { add(Calendar.MONTH, -meses) }
        return SimpleDateFormat(FORMATO_FECHA, Locale.US).format(cal.time)
    }

    fun aFechaHoraCorta(iso: String): String {
        val fecha = aFechaCorta(iso)
        val hora = iso.drop(11).take(5)
        return if (hora.length == 5) "$fecha $hora" else fecha
    }

    fun deFechaCortaAIso(texto: String): String? {
        val entrada = SimpleDateFormat("dd-MM-yyyy", Locale.US).apply { isLenient = false }
        return try {
            val fecha = entrada.parse(texto.trim()) ?: return null
            SimpleDateFormat(FORMATO_FECHA, Locale.US).format(fecha)
        } catch (e: ParseException) {
            null
        }
    }

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

    fun fechaCortaEnDias(dias: Int): String {
        val calendario = calendarioActual().apply { add(Calendar.DAY_OF_YEAR, dias) }
        return SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendario.time)
    }
}
