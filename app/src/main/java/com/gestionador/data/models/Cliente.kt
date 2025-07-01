package com.gestionador.data.models

data class Cliente(
    val id: String = "",
    val cedula: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", "", "")
}
