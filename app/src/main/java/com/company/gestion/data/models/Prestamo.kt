package com.company.gestion.data.models

enum class TipoPrestamo {
    DIARIO, SEMANAL, MENSUAL
}

enum class EstadoPrestamo {
    ACTIVO, PAGADO, VENCIDO
}

data class Prestamo(
    val id: String = "",
    val clienteId: String = "",
    val clienteNombre: String = "",
    val tipo: TipoPrestamo = TipoPrestamo.DIARIO,
    val fechaInicial: Long = System.currentTimeMillis(),
    val numeroCuota: Int = 0,
    val valorCuotaPactada: Double = 0.0,
    val porcentajeInteres: Double = 0.0, // Solo para préstamos mensuales
    val montoTotal: Double = 0.0,
    val saldoRestante: Double = 0.0,
    val estado: EstadoPrestamo = EstadoPrestamo.ACTIVO,
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", TipoPrestamo.DIARIO, 0L, 0, 0.0, 0.0, 0.0, 0.0, EstadoPrestamo.ACTIVO, 0L)
    
    fun getTipoString(): String {
        return when (tipo) {
            TipoPrestamo.DIARIO -> "Diario"
            TipoPrestamo.SEMANAL -> "Semanal"
            TipoPrestamo.MENSUAL -> "Mensual"
        }
    }
    
    fun getEstadoString(): String {
        return when (estado) {
            EstadoPrestamo.ACTIVO -> "Activo"
            EstadoPrestamo.PAGADO -> "Pagado"
            EstadoPrestamo.VENCIDO -> "Vencido"
        }
    }
}
