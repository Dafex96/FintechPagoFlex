package cl.duoc.model

enum class PasoRecuperacion {
    SOLICITUD_IDENTIFICADOR,
    VERIFICACION_CODIGO,
    NUEVA_CONTRASENA,
    EXITO
}

enum class ErrorRecuperacion {
    CAMPO_VACIO,
    IDENTIFICADOR_INVALIDO,
    USUARIO_NO_ENCONTRADO,
    CODIGO_INVALIDO,
    CONTRASENA_DEBIL,
    CONTRASENAS_DISTINTAS,
    GENERICO
}

data class RecuperarContrasenaUiState(
    val paso: PasoRecuperacion = PasoRecuperacion.SOLICITUD_IDENTIFICADOR,
    val identificador: String = "",
    val codigoVerificacion: String = "",
    val codigoGenerado: String = "",
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val usuarioEncontrado: Usuario? = null,

    val errorIdentificador: ErrorRecuperacion? = null,
    val errorCodigo: ErrorRecuperacion? = null,
    val errorNuevaContrasena: ErrorRecuperacion? = null,
    val errorConfirmarContrasena: ErrorRecuperacion? = null,
    val errorGeneral: ErrorRecuperacion? = null
)
