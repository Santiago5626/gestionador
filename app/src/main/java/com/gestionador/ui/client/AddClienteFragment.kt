package com.gestionador.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gestionador.data.models.Cliente
import com.gestionador.databinding.FragmentAddClienteBinding
import kotlinx.coroutines.launch

class AddClienteFragment : Fragment() {

    private var _binding: FragmentAddClienteBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddClienteViewModel by viewModels()
    private val clientesViewModel: ClientesViewModel by viewModels()
    
    private var clienteId: String? = null
    private var isEdit: Boolean = false
    private var currentCliente: Cliente? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Obtener argumentos
        clienteId = arguments?.getString("clienteId")
        isEdit = arguments?.getBoolean("isEdit", false) ?: false
        
        setupUI()
        setupObservers()
        setupClickListeners()
        
        if (isEdit && clienteId != null) {
            loadClienteData()
        }
    }
    
    private fun setupUI() {
        // Cambiar título según el modo
        binding.tvTitle.text = if (isEdit) "Editar Cliente" else "Agregar Cliente"
        binding.btnGuardar.text = if (isEdit) "Actualizar" else "Guardar"
    }
    
    private fun loadClienteData() {
        clientesViewModel.loadClientes()
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnGuardar.isEnabled = !isLoading
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.clienteCreated.collect { created ->
                if (created) {
                    val message = if (isEdit) "Cliente actualizado exitosamente" else "Cliente guardado exitosamente"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
        
        // Observer para cargar datos del cliente en modo edición
        if (isEdit) {
            viewLifecycleOwner.lifecycleScope.launch {
                clientesViewModel.clientes.collect { clientes ->
                    val cliente = clientes.find { it.id == clienteId }
                    cliente?.let {
                        currentCliente = it
                        fillClienteData(it)
                    }
                }
            }
        }
    }
    
    private fun fillClienteData(cliente: Cliente) {
        binding.etCedula.setText(cliente.cedula)
        binding.etNombre.setText(cliente.nombre)
        binding.etDireccion.setText(cliente.direccion)
        binding.etTelefono.setText(cliente.telefono)
    }
    
    private fun setupClickListeners() {
        binding.btnGuardar.setOnClickListener {
            val cedula = binding.etCedula.text.toString()
            val nombre = binding.etNombre.text.toString()
            val direccion = binding.etDireccion.text.toString()
            val telefono = binding.etTelefono.text.toString()
            
            if (validateInputs(cedula, nombre, direccion, telefono)) {
                val cliente = if (isEdit && currentCliente != null) {
                    // Actualizar cliente existente
                    currentCliente!!.copy(
                        cedula = cedula,
                        nombre = nombre,
                        direccion = direccion,
                        telefono = telefono
                    )
                } else {
                    // Crear nuevo cliente
                    Cliente(
                        cedula = cedula,
                        nombre = nombre,
                        apellido = "", // Campo vacío ya que no se usa
                        direccion = direccion,
                        telefono = telefono
                    )
                }
                viewModel.createCliente(cliente)
            }
        }
        
        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun validateInputs(cedula: String, nombre: String, direccion: String, telefono: String): Boolean {
        var isValid = true
        
        if (cedula.isBlank()) {
            binding.etCedula.error = "La cédula es requerida"
            isValid = false
        }
        
        if (nombre.isBlank()) {
            binding.etNombre.error = "El nombre es requerido"
            isValid = false
        }
        
        if (direccion.isBlank()) {
            binding.etDireccion.error = "La dirección es requerida"
            isValid = false
        }
        
        if (telefono.isBlank()) {
            binding.etTelefono.error = "El teléfono es requerido"
            isValid = false
        }
        
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
