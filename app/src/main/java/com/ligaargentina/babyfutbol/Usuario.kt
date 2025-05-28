package com.ligaargentina.babyfutbol

data class Usuario(
    val id: Int,
    var nombre: String,
    var apellido: String,
    var email: String,
    var edad: Int,
    var rol_id: Int = 2

    )
