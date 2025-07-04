data class Usuario(
    var id: Int,
    var nombre: String,
    var apellido: String,
    var email: String,
    var edad: Int,
    var roles_id: Int = 2,
    var arbitro_id: Int? = 5,
    var jugador_id: Int? = null,
    var entrenador_id: Int? = null


)
