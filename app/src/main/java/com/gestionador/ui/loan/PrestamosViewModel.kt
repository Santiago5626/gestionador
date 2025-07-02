    package com.gestionador.ui.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionador.data.models.Abono
import com.gestionador.data.models.Prestamo
import com.gestionador.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PrestamosViewModel : ViewModel() {
    
    private val repository = FirebaseRepository()
    
    private val _prestamos = MutableStateFlow<List<Prestamo>>(emptyList())
    val prestamos: StateFlow<List<Prestamo>> = _prestamos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadPrestamos(clienteId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getPrestamos(clienteId).collectLatest { prestamosList ->
                    // Recalcular intereses para préstamos mensuales vencidos sin abono
                    val updatedPrestamos = prestamosList.map { prestamo ->
                        if (prestamo.tipo == com.gestionador.data.models.TipoPrestamo.MENSUAL) {
                            val currentTime = System.currentTimeMillis()
                            val fiveDaysInMillis = 5 * 24 * 60 * 60 * 1000L
                            val timeSinceLastCalc = currentTime - prestamo.ultimaFechaCalculoInteres
                            if (timeSinceLastCalc > fiveDaysInMillis) {
                                // Aquí se debería verificar si hay abonos suficientes, pero para simplificar asumimos que no
                                val newInteres = prestamo.saldoRestante * prestamo.porcentajeInteres / 100
                                val nuevoSaldo = prestamo.saldoRestante + newInteres
                                prestamo.copy(
                                    saldoRestante = nuevoSaldo,
                                    interesesPendientes = prestamo.interesesPendientes + newInteres,
                                    ultimaFechaCalculoInteres = currentTime
                                )
                            } else {
                                prestamo
                            }
                        } else {
                            prestamo
                        }
                    }
                    _prestamos.value = updatedPrestamos
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deletePrestamo(prestamoId: String) {
        viewModelScope.launch {
            try {
                repository.deletePrestamo(prestamoId).let { result ->
                    result.fold(
                        onSuccess = { loadPrestamos() },
                        onFailure = { e -> _error.value = e.message }
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun realizarAbono(abono: Abono, prestamoActualizado: Prestamo) {
        viewModelScope.launch {
            try {
                // Crear el abono
                repository.guardarAbono(abono).let { result ->
                    result.fold(
                        onSuccess = {
                            // Actualizar el préstamo
                            repository.guardarPrestamo(prestamoActualizado).let { updateResult ->
                                updateResult.fold(
                                    onSuccess = { loadPrestamos() },
                                    onFailure = { e -> _error.value = e.message }
                                )
                            }
                        },
                        onFailure = { e -> _error.value = e.message }
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun obtenerAbonos(prestamoId: String) = repository.obtenerAbonos(prestamoId)
}
