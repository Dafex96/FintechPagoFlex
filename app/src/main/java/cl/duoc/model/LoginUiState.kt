package cl.duoc.model

enum class ErrorLogin {
    CAMPO_VACIO,
    RUT_INVALIDO,
    CREDENCIALES,
    INACTIVO,
    GENERICO
}

data class LoginUiState (
    val rut: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,

    val errorRut: ErrorLogin? = null,
    val errorContrasena: ErrorLogin? = null,
    val errorGeneral: ErrorLogin? = null
)
