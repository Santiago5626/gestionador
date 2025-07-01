package com.company.gestion.data.models

data class Abono(
    val id: String = "",
    val prestamoId: String = "",
    val clienteId: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val valorAbonado: Double = 0.0,
    val saldoRestante: Double = 0.0,
    val numeroCuota: Int = 0,
    val observaciones: String = ""
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", 0L, 0.0, 0.0, 0, "")
    
    fun getFechaFormateada(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(fecha))
    }
}
