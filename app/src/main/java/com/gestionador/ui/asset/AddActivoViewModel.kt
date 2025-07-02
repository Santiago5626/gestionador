package com.gestionador.ui.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionador.data.models.Activo
import com.gestionador.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddActivoViewModel : ViewModel() {
    
    private val repository = FirebaseRepository()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _activoCreated = MutableStateFlow(false)
    val activoCreated: StateFlow<Boolean> = _activoCreated.asStateFlow()
    
    fun createActivo(activo: Activo) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createActivo(activo).let { result ->
                    result.fold(
                        onSuccess = { 
                            _activoCreated.value = true
                            _error.value = null
                        },
                        onFailure = { e -> _error.value = e.message }
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
