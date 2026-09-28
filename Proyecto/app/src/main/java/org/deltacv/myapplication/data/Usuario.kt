package org.deltacv.myapplication.data

data class Usuario(
    val uid: String = "",
    val nombreCompleto: String = "",
    val usuario: String = "",
    val correo: String = "",
    val cum: String = "",
    val contrasena: String = "",
    val telefono: String? = null
)
