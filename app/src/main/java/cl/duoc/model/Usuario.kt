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
    val idEmpresa: Long? = null
)