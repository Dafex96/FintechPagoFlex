package cl.duoc.model

data class NuevoUsuario(
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String?,
    val comunaId: Long,
    val contrasenaHash: String
)