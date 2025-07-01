package com.gestionador.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionador.data.models.Cliente
import com.gestionador.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            repository.getClientes()
                .onSuccess { clientesList ->
                    _clientes.value = clientesList
                    _error.value = null
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            _isLoading.value = false
        }
    }
    
    fun deleteCliente(clienteId: String) {
        viewModelScope.launch {
            repository.deleteCliente(clienteId)
                .onSuccess {
                    loadClientes() // Reload the list
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
        }
    }
}
