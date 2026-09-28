package org.deltacv.myapplication

import org.deltacv.myapplication.data.RegistroValidator
import org.deltacv.myapplication.data.Usuario
import org.junit.Assert.*
import org.junit.Test

class RegistroTest {

    @Test
    fun validarFormulario_datosCorrectos_retornaValido() {
        val result = RegistroValidator.validarFormulario(
            nombreCompleto = "Ana Torres",
            usuario = "anatorres",
            correo = "ana.torres@example.com",
            cum = "CUM123",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )

        assertTrue(result.esValido)
        assertNull(result.errorGeneral)
        assertNull(result.errorConfirmacion)
    }

    @Test
    fun validarFormulario_camposVacios_retornaErrorGeneral() {
        val result = RegistroValidator.validarFormulario(
            nombreCompleto = "",
            usuario = "anatorres",
            correo = "ana.torres@example.com",
            cum = "CUM123",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )

        assertFalse(result.esValido)
        assertEquals("Por favor completa todos los campos.", result.errorGeneral)
    }

    @Test
    fun validarFormulario_contrasenasDiferentes_retornaErrorConfirmacion() {
        val result = RegistroValidator.validarFormulario(
            nombreCompleto = "Ana Torres",
            usuario = "anatorres",
            correo = "ana.torres@example.com",
            cum = "CUM123",
            contrasena = "123456",
            confirmarContrasena = "654321"
        )

        assertFalse(result.esValido)
        assertEquals("Las contraseñas no coinciden.", result.errorConfirmacion)
    }

    @Test
    fun validarFormulario_correoInvalido_retornaErrorCorreo() {
        val result = RegistroValidator.validarFormulario(
            nombreCompleto = "Ana Torres",
            usuario = "anatorres",
            correo = "correoInvalidoSinArroba",
            cum = "CUM123",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )

        assertFalse(result.esValido)
        assertEquals("Correo electrónico inválido", result.errorCorreo)
    }

    @Test
    fun verificarDuplicados_usuarioExiste_retornaErrorUsuario() {
        val listaExistente = listOf(
            Usuario(
                uid = "1",
                nombreCompleto = "Luis Ramirez",
                usuario = "anatorres",
                correo = "luis@example.com",
                cum = "CUM999",
                contrasena = "1234"
            )
        )

        val result = RegistroValidator.verificarDuplicados(
            usuario = "anatorres",
            correo = "nuevo@example.com",
            cum = "CUM000",
            listaExistente = listaExistente
        )

        assertFalse(result.esValido)
        assertEquals("Nombre de usuario inválido", result.errorUsuario)
        assertNull(result.errorCorreo)
        assertNull(result.errorCum)
    }

    @Test
    fun verificarDuplicados_correoExiste_retornaErrorCorreo() {
        val listaExistente = listOf(
            Usuario(
                uid = "1",
                nombreCompleto = "Luis Ramirez",
                usuario = "luisr",
                correo = "ana.torres@example.com",
                cum = "CUM999",
                contrasena = "1234"
            )
        )

        val result = RegistroValidator.verificarDuplicados(
            usuario = "anatorres",
            correo = "ana.torres@example.com",
            cum = "CUM000",
            listaExistente = listaExistente
        )

        assertFalse(result.esValido)
        assertEquals("Correo electrónico inválido", result.errorCorreo)
        assertNull(result.errorUsuario)
    }

    @Test
    fun verificarDuplicados_cumExiste_retornaErrorCum() {
        val listaExistente = listOf(
            Usuario(
                uid = "1",
                nombreCompleto = "Luis Ramirez",
                usuario = "luisr",
                correo = "luis@example.com",
                cum = "CUM123",
                contrasena = "1234"
            )
        )

        val result = RegistroValidator.verificarDuplicados(
            usuario = "anatorres",
            correo = "ana@example.com",
            cum = "cum123",
            listaExistente = listaExistente
        )

        assertFalse(result.esValido)
        assertEquals("CUM inválido", result.errorCum)
    }

    @Test
    fun verificarDuplicados_sinConflictos_retornaValido() {
        val listaExistente = listOf(
            Usuario(
                uid = "1",
                nombreCompleto = "Luis Ramirez",
                usuario = "luisr",
                correo = "luis@example.com",
                cum = "CUM123",
                contrasena = "1234"
            )
        )

        val result = RegistroValidator.verificarDuplicados(
            usuario = "anatorres",
            correo = "ana@example.com",
            cum = "CUM999",
            listaExistente = listaExistente
        )

        assertTrue(result.esValido)
        assertNull(result.errorUsuario)
        assertNull(result.errorCorreo)
        assertNull(result.errorCum)
    }

    @Test
    fun usuarioModel_creacionYPropiedades_correcto() {
        val user = Usuario(
            uid = "user_100",
            nombreCompleto = "Carlos Mendoza",
            usuario = "carlosm",
            correo = "carlos@example.com",
            cum = "CUM777",
            contrasena = "password123",
            telefono = "+52 614 999 8888"
        )

        assertEquals("user_100", user.uid)
        assertEquals("Carlos Mendoza", user.nombreCompleto)
        assertEquals("carlosm", user.usuario)
        assertEquals("carlos@example.com", user.correo)
        assertEquals("CUM777", user.cum)
        assertEquals("password123", user.contrasena)
        assertEquals("+52 614 999 8888", user.telefono)
    }
}
