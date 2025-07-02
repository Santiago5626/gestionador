package com.gestionador.ui.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                    _prestamos.value = prestamosList
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
}
