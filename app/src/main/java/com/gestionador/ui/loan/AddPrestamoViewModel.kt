package com.gestionador.ui.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionador.data.models.Prestamo
import com.gestionador.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddPrestamoViewModel : ViewModel() {
    
    private val repository = FirebaseRepository()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _prestamoCreated = MutableStateFlow(false)
    val prestamoCreated: StateFlow<Boolean> = _prestamoCreated.asStateFlow()
    
    fun createPrestamo(prestamo: Prestamo) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.guardarPrestamo(prestamo)
                result.fold(
                    onSuccess = {
                        _prestamoCreated.value = true
                        _error.value = null
                    },
                    onFailure = { e ->
                        _error.value = e.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
