package com.gestionador.data.models

data class Abono(
    val id: String = "",
    val prestamoId: String = "",
    val clienteId: String = "",
    val fechaAbono: Long = System.currentTimeMillis(),
    val valorAbonado: Double = 0.0,
    val saldoRestante: Double = 0.0,
    val numeroCuota: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", 0L, 0.0, 0.0, 0)
}
