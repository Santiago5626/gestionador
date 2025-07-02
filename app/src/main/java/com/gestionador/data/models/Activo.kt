package com.gestionador.data.models

enum class CategoriaActivo {
    INGRESO, GASTO, INVERSION
}

data class Activo(
    val id: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val montoIngresado: Double = 0.0,
    val descripcion: String = "",
    val categoria: CategoriaActivo = CategoriaActivo.INGRESO
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", 0L, 0.0, "", CategoriaActivo.INGRESO)
    
    fun getFechaFormateada(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(fecha))
    }
    
    fun getCategoriaString(): String {
        return when (categoria) {
            CategoriaActivo.INGRESO -> "Ingreso"
            CategoriaActivo.GASTO -> "Gasto"
            CategoriaActivo.INVERSION -> "Inversión"
        }
    }
    
    fun getMontoFormateado(): String {
        val signo = when (categoria) {
            CategoriaActivo.INGRESO -> "+"
            CategoriaActivo.GASTO -> "-"
            CategoriaActivo.INVERSION -> ""
        }
        return "$signo$${String.format("%.2f", montoIngresado)}"
    }
}
