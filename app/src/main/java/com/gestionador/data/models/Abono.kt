package com.gestionador.data.models

enum class TipoAbono {
    SOLO_INTERESES,    // Solo paga intereses (préstamos mensuales)
    CAPITAL_E_INTERESES, // Paga intereses y reduce capital
    CUOTA_NORMAL       // Para préstamos diarios y semanales
}

data class Abono(
    val id: String = "",
    val prestamoId: String = "",
    val numeroCuota: Int = 0, // Autoincrementable
    val fechaAbono: Long = System.currentTimeMillis(),
    val montoAbonado: Double = 0.0,
    val saldoRestante: Double = 0.0, // Capital restante después del abono
    val valorCuotaPactada: Double = 0.0, // Para referencia
    val reditosCalculados: Double = 0.0, // Intereses calculados en este período
    val interesesPagados: Double = 0.0, // Cuánto se pagó de intereses
    val capitalPagado: Double = 0.0, // Cuánto se pagó del capital
    val interesesPendientesAntes: Double = 0.0, // Intereses pendientes antes del abono
    val interesesPendientesDespues: Double = 0.0, // Intereses pendientes después del abono
    val tipoAbono: TipoAbono = TipoAbono.CUOTA_NORMAL,
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", 0, 0L, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, TipoAbono.CUOTA_NORMAL, 0L)
    
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
    
    fun getInteresesPagadosFormateado(): String {
        return "$${String.format("%.2f", interesesPagados)}"
    }
    
    fun getCapitalPagadoFormateado(): String {
        return "$${String.format("%.2f", capitalPagado)}"
    }
    
    fun getTipoAbonoString(): String {
        return when (tipoAbono) {
            TipoAbono.SOLO_INTERESES -> "Solo Intereses"
            TipoAbono.CAPITAL_E_INTERESES -> "Capital + Intereses"
            TipoAbono.CUOTA_NORMAL -> "Cuota Normal"
        }
    }
    
    // Función para convertir a Map para el cartón PDF
    fun toMap(): Map<String, Any> {
        val mapa = mutableMapOf<String, Any>(
            "Cuota #" to numeroCuota,
            "Fecha" to getFechaAbonoFormateada(),
            "Monto Abonado" to getMontoFormateado(),
            "Saldo Restante" to getSaldoFormateado()
        )
        
        // Para préstamos mensuales, agregar detalles de intereses
        if (tipoAbono != TipoAbono.CUOTA_NORMAL) {
            mapa["Tipo de Pago"] = getTipoAbonoString()
            mapa["Intereses Pagados"] = getInteresesPagadosFormateado()
            mapa["Capital Pagado"] = getCapitalPagadoFormateado()
            if (reditosCalculados > 0) {
                mapa["Intereses del Período"] = "$${String.format("%.2f", reditosCalculados)}"
            }
        } else {
            // Para préstamos diarios y semanales
            if (reditosCalculados > 0) {
                mapa["Réditos"] = "$${String.format("%.2f", reditosCalculados)}"
            }
        }
        
        return mapa
    }
    
    /**
     * Crea un abono para préstamos mensuales con el desglose de intereses y capital
     */
    companion object {
        fun crearAbonoMensual(
            prestamoId: String,
            numeroCuota: Int,
            montoAbonado: Double,
            interesesCalculados: Double,
            interesesPendientesAntes: Double,
            saldoCapitalAntes: Double,
            saldoCapitalDespues: Double,
            interesesPagados: Double,
            capitalPagado: Double,
            interesesPendientesDespues: Double
        ): Abono {
            val tipoAbono = if (capitalPagado > 0) {
                TipoAbono.CAPITAL_E_INTERESES
            } else {
                TipoAbono.SOLO_INTERESES
            }
            
            return Abono(
                id = "", // Se asignará en Firebase
                prestamoId = prestamoId,
                numeroCuota = numeroCuota,
                fechaAbono = System.currentTimeMillis(),
                montoAbonado = montoAbonado,
                saldoRestante = saldoCapitalDespues,
                valorCuotaPactada = 0.0, // No aplica para mensuales
                reditosCalculados = interesesCalculados,
                interesesPagados = interesesPagados,
                capitalPagado = capitalPagado,
                interesesPendientesAntes = interesesPendientesAntes,
                interesesPendientesDespues = interesesPendientesDespues,
                tipoAbono = tipoAbono,
                fechaCreacion = System.currentTimeMillis()
            )
        }
        
        fun crearAbonoNormal(
            prestamoId: String,
            numeroCuota: Int,
            montoAbonado: Double,
            saldoRestante: Double,
            valorCuotaPactada: Double
        ): Abono {
            return Abono(
                id = "", // Se asignará en Firebase
                prestamoId = prestamoId,
                numeroCuota = numeroCuota,
                fechaAbono = System.currentTimeMillis(),
                montoAbonado = montoAbonado,
                saldoRestante = saldoRestante,
                valorCuotaPactada = valorCuotaPactada,
                reditosCalculados = 0.0,
                interesesPagados = 0.0,
                capitalPagado = montoAbonado,
                interesesPendientesAntes = 0.0,
                interesesPendientesDespues = 0.0,
                tipoAbono = TipoAbono.CUOTA_NORMAL,
                fechaCreacion = System.currentTimeMillis()
            )
        }
    }
}
