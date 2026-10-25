package cl.duoc.notificaciones

import android.content.Context
import androidx.core.content.edit
import cl.duoc.util.Fechas

/**
 * Recuerda qué avisos ya se enviaron para no repetirlos. Garantiza "como máximo un aviso por
 * compromiso al día" (RN-15) y que cada situación se avise una sola vez.
 */
class RegistroAvisos(context: Context) {

    private val prefs = context.getSharedPreferences("avisos_enviados", Context.MODE_PRIVATE)

    fun yaEnviado(clave: String): Boolean = prefs.getBoolean("evento_$clave", false)

    fun puedeAvisarHoy(idCompromiso: Long): Boolean =
        prefs.getString("dia_$idCompromiso", null) != Fechas.hoyIso()

    fun registrar(clave: String, idCompromiso: Long) {
        prefs.edit {
            putBoolean("evento_$clave", true)
            putString("dia_$idCompromiso", Fechas.hoyIso())
        }
    }
}
