package cl.duoc.model

data class CuentaDemo(
    val nombre: String,
    val rut: String,
    val rolCodigo: String
) {
    companion object {
        val TODAS = listOf(
            CuentaDemo("Camila Rojas", "15834207-3", RolCodigo.USUARIO_FINAL),
            CuentaDemo("Andrés Lagos", "16248317-K", RolCodigo.AGENTE),
            CuentaDemo("Paula Saavedra", "14629853-2", RolCodigo.EJECUTIVO)
        )
    }
}