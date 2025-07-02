package com.gestionador.ui.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionador.data.models.Activo
import com.gestionador.data.models.CategoriaActivo
import com.gestionador.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ActivosViewModel : ViewModel() {
    
    private val repository = FirebaseRepository()
    
    private val _activos = MutableStateFlow<List<Activo>>(emptyList())
    val activos: StateFlow<List<Activo>> = _activos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Calculate total balance based on activos
    val totalBalance: StateFlow<Double> = _activos.combine(_activos) { activos, _ ->
        activos.sumOf { activo ->
            when (activo.categoria) {
                CategoriaActivo.INGRESO -> activo.montoIngresado
                CategoriaActivo.GASTO -> -activo.montoIngresado
                CategoriaActivo.INVERSION -> activo.montoIngresado
            }
        }
    }.asStateFlow()
    
    fun loadActivos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getActivos().collectLatest { activosList ->
                    _activos.value = activosList
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteActivo(activoId: String) {
        viewModelScope.launch {
            try {
                repository.deleteActivo(activoId).let { result ->
                    result.fold(
                        onSuccess = { loadActivos() },
                        onFailure = { e -> _error.value = e.message }
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
