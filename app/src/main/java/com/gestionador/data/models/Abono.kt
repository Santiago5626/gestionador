package com.gestionador.data.models

data class Abono(
    val id: String = "",
    val prestamoId: String = "",
    val numeroCuota: Int = 0, // Autoincrementable
    val fechaAbono: Long = System.currentTimeMillis(),
    val montoAbonado: Double = 0.0,
    val saldoRestante: Double = 0.0, // Cuánto resta hasta el momento
    val valorCuotaPactada: Double = 0.0, // Para referencia
    val reditosCalculados: Double = 0.0, // Solo para préstamos mensuales
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", 0, 0L, 0.0, 0.0, 0.0, 0.0, 0L)
    
    fun getFechaAbonoFormateada(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(fechaAbono))
    }
    
    fun getMontoFormateado(): String {
        return "$${String.format("%.2f", montoAbonado)}"
    }
    
    fun getSaldoFormateado(): String {
        return "$${String.format("%.2f", saldoRestante)}"
    }
    
    // Función para convertir a Map para el cartón PDF
    fun toMap(): Map<String, Any> {
        return mapOf(
            "Cuota #" to numeroCuota,
            "Fecha" to getFechaAbonoFormateada(),
            "Monto Abonado" to getMontoFormateado(),
            "Saldo Restante" to getSaldoFormateado(),
            "Réditos" to if (reditosCalculados > 0) "$${String.format("%.2f", reditosCalculados)}" else "N/A"
        )
    }
}
