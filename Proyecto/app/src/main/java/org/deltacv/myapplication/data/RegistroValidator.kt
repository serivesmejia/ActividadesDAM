package org.deltacv.myapplication.data

data class ValidacionRegistroResult(
    val esValido: Boolean,
    val errorNombre: String? = null,
    val errorUsuario: String? = null,
    val errorCorreo: String? = null,
    val errorCum: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null,
    val errorGeneral: String? = null
)

object RegistroValidator {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun esCorreoValido(correo: String): Boolean {
        return correo.isNotBlank() && emailRegex.matches(correo.trim())
    }

    fun esCumValido(cum: String): Boolean {
        return cum.isNotBlank() && cum.all { it.isLetterOrDigit() }
    }

    fun validarFormulario(
        nombreCompleto: String,
        usuario: String,
        correo: String,
        cum: String,
        contrasena: String,
        confirmarContrasena: String
    ): ValidacionRegistroResult {
        if (nombreCompleto.isBlank() || usuario.isBlank() || correo.isBlank() ||
            cum.isBlank() || contrasena.isBlank() || confirmarContrasena.isBlank()
        ) {
            return ValidacionRegistroResult(
                esValido = false,
                errorGeneral = "Por favor completa todos los campos."
            )
        }

        if (!esCorreoValido(correo)) {
            return ValidacionRegistroResult(
                esValido = false,
                errorCorreo = "Correo electrónico inválido"
            )
        }

        if (!esCumValido(cum)) {
            return ValidacionRegistroResult(
                esValido = false,
                errorCum = "CUM inválido"
            )
        }

        if (contrasena != confirmarContrasena) {
            return ValidacionRegistroResult(
                esValido = false,
                errorConfirmacion = "Las contraseñas no coinciden."
            )
        }

        return ValidacionRegistroResult(esValido = true)
    }

    fun verificarDuplicados(
        usuario: String,
        correo: String,
        cum: String,
        listaExistente: List<Usuario>
    ): ValidacionRegistroResult {
        val usuarioExiste = listaExistente.any { it.usuario.trim().equals(usuario.trim(), ignoreCase = true) }
        val correoExiste = listaExistente.any { it.correo.trim().equals(correo.trim(), ignoreCase = true) }
        val cumExiste = listaExistente.any { it.cum.trim().equals(cum.trim(), ignoreCase = true) }

        if (usuarioExiste || correoExiste || cumExiste) {
            return ValidacionRegistroResult(
                esValido = false,
                errorUsuario = if (usuarioExiste) "Nombre de usuario inválido" else null,
                errorCorreo = if (correoExiste) "Correo electrónico inválido" else null,
                errorCum = if (cumExiste) "CUM inválido" else null
            )
        }

        return ValidacionRegistroResult(esValido = true)
    }
}
