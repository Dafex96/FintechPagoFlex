package cl.duoc.util

import java.security.MessageDigest

object Seguridad {
    private const val SAL_DEMO = "pagoflex-demo:"

    fun hash(contrasena: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest((SAL_DEMO + contrasena).toByteArray())
            .joinToString("") { "%02x".format(it) }
}