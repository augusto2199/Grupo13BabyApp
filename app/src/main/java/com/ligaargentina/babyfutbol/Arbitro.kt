package com.ligaargentina.babyfutbol

data class Arbitro(
    val id: Int,
    var nombre: String,
    var apellido: String,
    var email: String,
    var edad: Int,
    var experiencia: Int,
    var roles_id: Int = 5
)
