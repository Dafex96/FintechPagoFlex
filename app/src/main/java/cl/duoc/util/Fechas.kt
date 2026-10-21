package cl.duoc.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Fechas {

    private const val FORMATO_FECHA = "yyyy-MM-dd"
    private const val FORMATO_FECHA_HORA = "yyyy-MM-dd'T'HH:mm:ss"

    fun hoyIso(): String = SimpleDateFormat(FORMATO_FECHA, Locale.US).format(Date())

    /** "2026-10-21T14:30:00" */
    fun ahoraIso(): String = SimpleDateFormat(FORMATO_FECHA_HORA, Locale.US).format(Date())
}