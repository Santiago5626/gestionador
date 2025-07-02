package com.gestionador.ui.asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gestionador.data.models.Activo
import com.gestionador.data.models.CategoriaActivo
import com.gestionador.databinding.FragmentAddActivoBinding
import kotlinx.coroutines.launch

class AddActivoFragment : Fragment() {

    private var _binding: FragmentAddActivoBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddActivoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddActivoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupSpinner()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupSpinner() {
        val categorias = CategoriaActivo.values().map { it.displayName }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categorias
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = adapter
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.buttonGuardar.isEnabled = !isLoading
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
            viewModel.activoCreated.collect { created ->
                if (created) {
                    Toast.makeText(requireContext(), "Activo guardado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonGuardar.setOnClickListener {
            val monto = binding.editTextMonto.text.toString().toDoubleOrNull()
            val descripcion = binding.editTextDescripcion.text.toString()
            val selectedIndex = binding.spinnerCategoria.selectedItemPosition
            val categoria = CategoriaActivo.values()[selectedIndex]
            
            if (validateInputs(monto, descripcion)) {
                val activo = Activo(
                    fecha = System.currentTimeMillis(),
                    montoIngresado = monto!!,
                    descripcion = descripcion,
                    categoria = categoria
                )
                viewModel.createActivo(activo)
            }
        }
    }
    
    private fun validateInputs(monto: Double?, descripcion: String): Boolean {
        var isValid = true
        
        if (monto == null || monto <= 0) {
            binding.editTextMonto.error = "El monto debe ser mayor a 0"
            isValid = false
        }
        
        if (descripcion.isBlank()) {
            binding.editTextDescripcion.error = "La descripción es requerida"
            isValid = false
        }
        
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
