package cl.duoc.model

object RolCodigo {
    const val USUARIO_FINAL = "USUARIO_FINAL"
    const val AGENTE = "AGENTE"
    const val EJECUTIVO = "EJECUTIVO"
}

object EstadoCompromisoId {
    const val PENDIENTE = 1
    const val VENCIDO = 2
    const val PAGADO = 3
    const val EN_REVISION = 4
    const val ANULADO = 5
}

object CanalId {
    const val APP = 1
    const val PORTAL_PAGOS = 2
    const val AGENTE_CONVERSACIONAL = 3
}

object MotivoAnulacionId {
    const val REPACTACION = 1
    const val ERROR_REGISTRO = 2
    const val PAGADO_DIRECTO = 3
    const val OTRO = 4
}

object MotivoReporteId {
    const val YA_PAGUE_OTRO_MEDIO = 1
    const val NO_RECONOZCO_COBRO = 2
    const val MONTO_NO_CORRESPONDE = 3
    const val OTRO = 4
}

object EstadoReporteId {
    const val ABIERTO = 1
    const val EN_GESTION = 2
    const val RESUELTO = 3
}

object EstadoRecomendacionId {
    const val RECIBIDA = 1
    const val CONTACTADA = 2
    const val EN_EVALUACION = 3
    const val SE_UNIO = 4
    const val DESCARTADA = 5
}

object AvisoAnticipacion {
    val OPCIONES = listOf(1, 3, 5)
    const val POR_DEFECTO = 3
}

const val MAX_RECOMENDACIONES_POR_MES = 5