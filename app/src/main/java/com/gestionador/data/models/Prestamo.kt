package com.gestionador.data.models

data class Prestamo(
    val id: String = "",
    val clienteId: String = "",
    val tipo: TipoPrestamo = TipoPrestamo.DIARIO,
    val fechaInicial: Long = System.currentTimeMillis(),
    val numeroCuota: Int = 0,
    val valorCuotaPactada: Double = 0.0,
    val porcentajeInteres: Double = 0.0, // Solo para mensual
    val montoTotal: Double = 0.0,
    val saldoRestante: Double = 0.0,
    val estado: EstadoPrestamo = EstadoPrestamo.ACTIVO,
    val historialAbonos: List<String> = emptyList(), // IDs de abonos
    val createdAt: Long = System.currentTimeMillis()
) {
    // Empty constructor for Firestore
    constructor() : this("", "", TipoPrestamo.DIARIO, 0L, 0, 0.0, 0.0, 0.0, 0.0, EstadoPrestamo.ACTIVO, emptyList())
}

enum class TipoPrestamo {
    DIARIO,
    SEMANAL,
    MENSUAL
}

enum class EstadoPrestamo {
    ACTIVO,
    PAGADO,
    VENCIDO
}
