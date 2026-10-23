package cl.duoc.model

enum class ErrorPerfil {
    CAMPO_VACIO,
    CORREO_INVALIDO,
    CORREO_EXISTE,
    TELEFONO_INVALIDO,
    COMUNA_NO_ELEGIDA,
    GENERICO
}

data class EditarPerfilUiState(
    val rut: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val telefono: String = "",
    val comunaId: Long? = null,

    val comunas: List<Comuna> = emptyList(),
    val pideComuna: Boolean = false,

    val errorNombres: ErrorPerfil? = null,
    val errorApellidos: ErrorPerfil? = null,
    val errorCorreo: ErrorPerfil? = null,
    val errorTelefono: ErrorPerfil? = null,
    val errorComuna: ErrorPerfil? = null,
    val errorGeneral: ErrorPerfil? = null
) {
    val comunaNombre: String
        get() = comunas.firstOrNull { it.id == comunaId }?.nombre ?: ""
}