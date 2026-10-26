package cl.duoc.util

import cl.duoc.R
import cl.duoc.model.NuevoCompromisoEmpresa

object ValidadorCompromiso {

    fun validar(n: NuevoCompromisoEmpresa, personaExiste: Boolean): Int? {
        if (n.rutPersona.isBlank() || n.concepto.isBlank() ||
            n.monto.isBlank() || n.fechaVencimiento.isBlank()
        ) return R.string.error_campo_obligatorio

        if (!Rut.esValido(n.rutPersona)) return R.string.error_rut_invalido

        val monto = n.monto.trim().toIntOrNull()
        if (monto == null || monto <= 0) return R.string.error_monto_invalido

        val fechaIso = Fechas.deFechaCortaAIso(n.fechaVencimiento) ?: return R.string.error_fecha_invalida
        if (fechaIso < Fechas.hoyIso()) return R.string.error_fecha_pasada

        if (n.numeroCuota.isNotBlank() != n.totalCuotas.isNotBlank()) return R.string.error_campo_obligatorio
        if (n.numeroCuota.isNotBlank()) {
            val cuota = n.numeroCuota.trim().toIntOrNull()
            val total = n.totalCuotas.trim().toIntOrNull()
            if (cuota == null || total == null || cuota < 1 || cuota > total) {
                return R.string.error_cuota_mayor_total
            }
        }

        if (!personaExiste &&
            (n.nombresPersona.isBlank() || n.apellidosPersona.isBlank() || n.correoPersona.isBlank())
        ) return R.string.error_campo_obligatorio

        return null
    }
}