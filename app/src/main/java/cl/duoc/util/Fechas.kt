package cl.duoc.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Fechas {

    private const val FORMATO_FECHA = "yyyy-MM-dd"
    private const val FORMATO_FECHA_HORA = "yyyy-MM-dd'T'HH:mm:ss"

    fun hoyIso(): String = SimpleDateFormat(FORMATO_FECHA, Locale.US).format(Date())

    fun ahoraIso(): String = SimpleDateFormat(FORMATO_FECHA_HORA, Locale.US).format(Date())

    fun aFechaCorta(iso: String): String {
        val partes = iso.take(10).split("-")
        return if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else iso
    }
}