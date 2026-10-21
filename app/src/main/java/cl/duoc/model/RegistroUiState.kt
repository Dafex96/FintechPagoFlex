package cl.duoc.model

enum class ErrorRegistro {
    CAMPO_VACIO,
    RUT_INVALIDO,
    RUT_EXISTE,
    CORREO_INVALIDO,
    CORREO_EXISTE,
    TELEFONO_INVALIDO,
    COMUNA_NO_ELEGIDA,
    CONTRASENA_DEBIL,
    CONTRASENAS_DISTINTAS,
    TERMINOS_NO_ACEPTADOS,
    GENERICO
}

data class RegistroUiState(
    val nombres: String = "",
    val apellidos: String = "",
    val rut: String = "",
    val correo: String = "",
    val telefono: String = "",
    val comunaId: Long? = null,
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val aceptaTerminos: Boolean = false,

    val comunas: List<Comuna> = emptyList(),

    val errorNombres: ErrorRegistro? = null,
    val errorApellidos: ErrorRegistro? = null,
    val errorRut: ErrorRegistro? = null,
    val errorCorreo: ErrorRegistro? = null,
    val errorTelefono: ErrorRegistro? = null,
    val errorComuna: ErrorRegistro? = null,
    val errorContrasena: ErrorRegistro? = null,
    val errorConfirmar: ErrorRegistro? = null,
    val errorTerminos: ErrorRegistro? = null,
    val errorGeneral: ErrorRegistro? = null
) {
    val comunaNombre: String
        get() = comunas.firstOrNull { it.id == comunaId }?.nombre ?: ""
}