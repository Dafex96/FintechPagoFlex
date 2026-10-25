package cl.duoc.notificaciones

import android.content.Context
import androidx.core.content.edit
import cl.duoc.util.Fechas

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
