package cl.duoc.model

data class NuevoCompromisoEmpresa(
    val rutPersona: String = "",
    val nombresPersona: String = "",
    val apellidosPersona: String = "",
    val correoPersona: String = "",
    val telefonoPersona: String = "",
    val concepto: String = "",
    val monto: String = "",
    val fechaVencimiento: String = "",
    val numeroCuota: String = "",
    val totalCuotas: String = ""
)
