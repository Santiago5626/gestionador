package com.gestionador.ui.asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gestionador.data.models.Activo
import com.gestionador.databinding.FragmentAddActivoBinding
import kotlinx.coroutines.launch

class AddActivoFragment : Fragment() {

    private var _binding: FragmentAddActivoBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddActivoViewModel by viewModels()
    private val activosViewModel: ActivosViewModel by viewModels()
    
    private var activoId: String? = null
    private var isEdit: Boolean = false
    private var currentActivo: Activo? = null

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
        
        // Obtener argumentos
        activoId = arguments?.getString("activoId")
        isEdit = arguments?.getBoolean("isEdit", false) ?: false
        
        setupUI()
        setupObservers()
        setupClickListeners()
        
        if (isEdit && activoId != null) {
            loadActivoData()
        }
    }
    
    private fun setupUI() {
        // Cambiar título según el modo
        binding.textViewTitle.text = if (isEdit) "Editar Activo" else "Agregar Activo"
        binding.buttonGuardar.text = if (isEdit) "Actualizar" else "Guardar"
    }
    
    private fun loadActivoData() {
        activosViewModel.loadActivos()
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
                    val message = if (isEdit) "Activo actualizado exitosamente" else "Activo guardado exitosamente"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
        
        // Observer para cargar datos del activo en modo edición
        if (isEdit) {
            viewLifecycleOwner.lifecycleScope.launch {
                activosViewModel.activos.collect { activos ->
                    val activo = activos.find { it.id == activoId }
                    activo?.let {
                        currentActivo = it
                        fillActivoData(it)
                    }
                }
            }
        }
    }
    
    private fun fillActivoData(activo: Activo) {
        binding.editTextMonto.setText(activo.monto.toString())
        binding.editTextProcedencia.setText(activo.procedencia)
        binding.editTextDescripcion.setText(activo.descripcion)
    }
    
    private fun setupClickListeners() {
        binding.buttonGuardar.setOnClickListener {
            val monto = binding.editTextMonto.text.toString().toDoubleOrNull()
            val procedencia = binding.editTextProcedencia.text.toString()
            val descripcion = binding.editTextDescripcion.text.toString()
            
            if (validateInputs(monto, procedencia, descripcion)) {
                val activo = if (isEdit && currentActivo != null) {
                    // Actualizar activo existente
                    currentActivo!!.copy(
                        monto = monto!!,
                        procedencia = procedencia,
                        descripcion = descripcion
                    )
                } else {
                    // Crear nuevo activo
                    Activo(
                        monto = monto!!,
                        procedencia = procedencia,
                        descripcion = descripcion
                    )
                }
                viewModel.createActivo(activo)
            }
        }
    }
    
    private fun validateInputs(monto: Double?, procedencia: String, descripcion: String): Boolean {
        var isValid = true
        
        if (monto == null || monto <= 0) {
            binding.editTextMonto.error = "El monto debe ser mayor a 0"
            isValid = false
        }
        
        if (procedencia.isBlank()) {
            binding.editTextProcedencia.error = "La procedencia es requerida"
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
