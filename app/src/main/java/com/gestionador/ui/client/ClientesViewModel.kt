package com.gestionador.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionador.data.models.Cliente
import com.gestionador.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClientesViewModel : ViewModel() {
    
    private val repository = FirebaseRepository()
    
    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> = _clientes.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadClientes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getClientes().collectLatest { clientesList ->
                    _clientes.value = clientesList
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteCliente(clienteId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCliente(clienteId).let { result ->
                    result.fold(
                        onSuccess = { loadClientes() },
                        onFailure = { e -> _error.value = e.message }
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
