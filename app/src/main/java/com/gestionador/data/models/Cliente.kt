package com.gestionador.data.models

data class Cliente(
    val id: String = "",
    val cedula: String = "",
    val nombre: String = "",
    val apellido: String = "", // Mantenemos para compatibilidad con Firebase, pero no se usará
    val direccion: String = "",
    val telefono: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "", "", "", 0L)
    
    fun getNombreCompleto(): String {
        return nombre // Solo devuelve el nombre, sin apellido
    }
}
