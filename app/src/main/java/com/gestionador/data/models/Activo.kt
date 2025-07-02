package com.gestionador.data.models

import java.util.Date

data class Activo(
    val id: String = "",
    val monto: Double = 0.0,
    val procedencia: String = "",
    val descripcion: String = "",
    val fechaCreacion: Date = Date()
) {
    constructor() : this("", 0.0, "", "", Date())
}
