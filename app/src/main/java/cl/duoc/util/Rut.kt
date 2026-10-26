package cl.duoc.util

object Rut {

    fun normalizar(texto: String): String {
        val limpio = texto.filter { it.isLetterOrDigit() }.uppercase()
        if (limpio.length < 2) return limpio
        return limpio.dropLast(1) + "-" + limpio.last()
    }

    fun esValido(texto: String): Boolean {
        val partes = normalizar(texto).split("-")
        if (partes.size != 2) return false
        val cuerpo = partes[0]
        val dv = partes[1]
        if (cuerpo.length !in 6..8 || !cuerpo.all { it.isDigit() }) return false
        return calcularDv(cuerpo) == dv
    }

    fun calcularDv(cuerpo: String): String {
        var suma = 0
        var factor = 2
        for (i in cuerpo.length - 1 downTo 0) {
            suma += (cuerpo[i] - '0') * factor
            factor = if (factor == 7) 2 else factor + 1
        }
        return when (val resto = 11 - (suma % 11)) {
            11 -> "0"
            10 -> "K"
            else -> resto.toString()
        }
    }

    fun enmascarar(texto: String): String {
        val normalizado = normalizar(texto)
        val cuerpo = normalizado.substringBefore("-")
        val dv = normalizado.substringAfter("-", "")
        if (dv.isEmpty() || cuerpo.length < 2) return normalizado
        val inicio = cuerpo.dropLast(6).ifEmpty { cuerpo.take(1) }
        return "$inicio.*.*-$dv"
    }
}