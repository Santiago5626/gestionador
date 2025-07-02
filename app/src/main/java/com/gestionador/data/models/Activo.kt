package com.gestionador.data.models

data class Activo(
    val id: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val montoIngresado: Double = 0.0,
    val descripcion: String = "",
    val categoria: CategoriaActivo = CategoriaActivo.INGRESOS,
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", 0L, 0.0, "", CategoriaActivo.INGRESOS, 0L)
    
    fun getFechaFormateada(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(fecha))
    }
    
    fun getMontoFormateado(): String {
        return "$${String.format("%.2f", montoIngresado)}"
    }

    // Para reportes estadísticos
    fun toReportData(): Map<String, Any> {
        return mapOf(
            "fecha" to getFechaFormateada(),
            "monto" to montoIngresado,
            "descripcion" to descripcion,
            "categoria" to categoria.displayName
        )
    }
}
