package com.gestionador.data.models

data class Activo(
    val id: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val montoIngresado: Double = 0.0,
    val descripcion: String = "",
    val categoria: CategoriaActivo = CategoriaActivo.INGRESO,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Empty constructor for Firestore
    constructor() : this("", 0L, 0.0, "", CategoriaActivo.INGRESO)
}

enum class CategoriaActivo {
    INGRESO,
    GASTO,
    INVERSION
}
