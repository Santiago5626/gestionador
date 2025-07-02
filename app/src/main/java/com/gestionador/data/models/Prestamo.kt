package com.gestionador.data.models

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
    val numeroCuota: Int = 1, // Autoincrementable
    val valorCuotaPactada: Double = 0.0,
    val porcentajeInteres: Double = 0.0, // Solo para préstamos mensuales
    val montoTotal: Double = 0.0,
    val saldoRestante: Double = 0.0,
    val estado: EstadoPrestamo = EstadoPrestamo.ACTIVO,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val abonos: List<Abono> = emptyList()
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", TipoPrestamo.DIARIO, 0L, 1, 0.0, 0.0, 0.0, 0.0, EstadoPrestamo.ACTIVO, 0L)
    
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

    // Función para recalcular réditos después de un abono (solo para préstamos mensuales)
    fun recalcularReditos(nuevoSaldo: Double): Double {
        return if (tipo == TipoPrestamo.MENSUAL) {
            nuevoSaldo * (porcentajeInteres / 100)
        } else {
            0.0
        }
    }

    // Función para generar datos del cartón (tabla de información del préstamo)
    fun generarDatosCarton(): Map<String, Any> {
        return mapOf(
            "Cliente" to clienteNombre,
            "Tipo de Préstamo" to getTipoString(),
            "Fecha Inicial" to fechaInicial,
            "Monto Total" to montoTotal,
            "Valor Cuota Pactada" to valorCuotaPactada,
            "Saldo Restante" to saldoRestante,
            "Estado" to getEstadoString(),
            "Número de Cuota Actual" to numeroCuota,
            "Porcentaje de Interés" to if (tipo == TipoPrestamo.MENSUAL) "$porcentajeInteres%" else "N/A",
            "Historial de Abonos" to abonos.map { it.toMap() }
        )
    }
}
