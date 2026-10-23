package cl.duoc.model

data class Usuario(
    val id: Long = 0,
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val rolId: Long,
    val rolCodigo: String,
    val activo: Boolean = true,
    val idEmpresa: Long? = null,

    //Perfil
    val telefono: String? = null,
    val comunaId: Long? = null,
    val comunaNombre: String? = null,
    val fechaIncorporacion: String? = null,
    val areaNombre: String? = null,
    val empresaNombre: String? = null,
    val cargo: String? = null
) {
    val primerNombre: String get() = nombres.substringBefore(" ")

    val nombreCompleto: String get() = "$nombres $apellidos"

    val iniciales: String
        get() = (nombres.firstOrNull()?.toString() ?: "") + (apellidos.firstOrNull()?.toString() ?: "")
}