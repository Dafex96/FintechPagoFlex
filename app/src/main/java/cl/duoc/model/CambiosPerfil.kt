package cl.duoc.model

data class CambiosPerfil(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String?,
    val comunaId: Long?
)