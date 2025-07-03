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
    val valorDevolver: Double = 0.0, // Renombrado de valorCuotaPactada a valorDevolver
    val porcentajeInteres: Double = 0.0, // Solo para préstamos mensuales
    val montoTotal: Double = 0.0, // Capital inicial para préstamos mensuales
    val saldoRestante: Double = 0.0, // Capital actual (puede aumentar si no se pagan intereses)
    val interesesPendientes: Double = 0.0, // Intereses acumulados no pagados
    val ultimaFechaCalculoInteres: Long = System.currentTimeMillis(), // Para calcular intereses mensuales
    val estado: EstadoPrestamo = EstadoPrestamo.ACTIVO,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val abonos: List<Abono> = emptyList()
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", TipoPrestamo.DIARIO, 0L, 1, 0.0, 0.0, 0.0, 0.0, 0.0, 0L, EstadoPrestamo.ACTIVO, 0L)
    
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

    /**
     * Calcula los intereses mensuales sobre el capital actual (saldoRestante)
     * Solo aplica para préstamos mensuales
     */
    fun calcularInteresesMensuales(): Double {
        return if (tipo == TipoPrestamo.MENSUAL) {
            saldoRestante * (porcentajeInteres / 100)
        } else {
            0.0
        }
    }

    /**
     * Procesa un abono para préstamos mensuales:
     * 1. Primero paga intereses pendientes
     * 2. Luego reduce el capital (saldoRestante)
     * 3. Si no se pagan todos los intereses, se suman al capital
     */
    fun procesarAbonoMensual(montoAbono: Double): Prestamo {
        if (tipo != TipoPrestamo.MENSUAL) {
            // Para préstamos diarios y semanales, funciona como antes
            return this.copy(
                saldoRestante = maxOf(0.0, saldoRestante - montoAbono),
                numeroCuota = numeroCuota + 1
            )
        }

        val interesesActuales = calcularInteresesMensuales()
        val totalInteresesPendientes = interesesPendientes + interesesActuales
        
        var nuevoSaldoRestante = saldoRestante
        var nuevosInteresesPendientes = totalInteresesPendientes
        
        if (montoAbono >= totalInteresesPendientes) {
            // El abono cubre todos los intereses
            val sobrante = montoAbono - totalInteresesPendientes
            nuevosInteresesPendientes = 0.0
            nuevoSaldoRestante = maxOf(0.0, saldoRestante - sobrante)
        } else {
            // El abono no cubre todos los intereses
            nuevosInteresesPendientes = totalInteresesPendientes - montoAbono
            // Los intereses no pagados se suman al capital
            nuevoSaldoRestante = saldoRestante + nuevosInteresesPendientes
            nuevosInteresesPendientes = 0.0
        }

        return this.copy(
            saldoRestante = nuevoSaldoRestante,
            interesesPendientes = nuevosInteresesPendientes,
            numeroCuota = numeroCuota + 1,
            ultimaFechaCalculoInteres = System.currentTimeMillis()
        )
    }

    /**
     * Actualiza los intereses pendientes si ha pasado un mes desde el último cálculo
     * Solo para préstamos mensuales
     */
    fun actualizarInteresesSiCorresponde(): Prestamo {
        if (tipo != TipoPrestamo.MENSUAL) return this
        
        val ahora = System.currentTimeMillis()
        val unMesEnMillis = 30L * 24L * 60L * 60L * 1000L // Aproximadamente 30 días
        
        return if (ahora - ultimaFechaCalculoInteres >= unMesEnMillis) {
            val nuevosIntereses = calcularInteresesMensuales()
            this.copy(
                interesesPendientes = interesesPendientes + nuevosIntereses,
                ultimaFechaCalculoInteres = ahora
            )
        } else {
            this
        }
    }

    /**
     * Obtiene el monto mínimo a pagar (solo intereses para préstamos mensuales)
     */
    fun getMontoMinimoPago(): Double {
        return when (tipo) {
            TipoPrestamo.MENSUAL -> {
                val prestamosActualizado = actualizarInteresesSiCorresponde()
                prestamosActualizado.interesesPendientes + prestamosActualizado.calcularInteresesMensuales()
            }
            else -> valorDevolver
        }
    }

    /**
     * Obtiene el saldo total (capital + intereses pendientes)
     */
    fun getSaldoTotal(): Double {
        return if (tipo == TipoPrestamo.MENSUAL) {
            saldoRestante + interesesPendientes
        } else {
            saldoRestante
        }
    }

    // Función para generar datos del cartón (tabla de información del préstamo)
    fun generarDatosCarton(): Map<String, Any> {
        val datos = mutableMapOf<String, Any>(
            "Cliente" to clienteNombre,
            "Tipo de Préstamo" to getTipoString(),
            "Fecha Inicial" to fechaInicial,
            "Monto Total" to montoTotal,
            "Saldo Restante" to saldoRestante,
            "Estado" to getEstadoString(),
            "Número de Cuota Actual" to numeroCuota,
            "Historial de Abonos" to abonos.map { it.toMap() }
        )

        if (tipo == TipoPrestamo.MENSUAL) {
            datos["Porcentaje de Interés"] = "$porcentajeInteres%"
            datos["Intereses Pendientes"] = interesesPendientes
            datos["Saldo Total (Capital + Intereses)"] = getSaldoTotal()
            datos["Pago Mínimo (Solo Intereses)"] = getMontoMinimoPago()
        } else {
            datos["Valor a Devolver"] = valorDevolver
        }

        return datos
    }
}
