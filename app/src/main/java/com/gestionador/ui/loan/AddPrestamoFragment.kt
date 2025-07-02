package com.gestionador.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gestionador.data.models.Prestamo
import com.gestionador.databinding.FragmentAddPrestamoBinding
import kotlinx.coroutines.launch

class AddPrestamoFragment : Fragment() {

    private var _binding: FragmentAddPrestamoBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddPrestamoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPrestamoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
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
            viewModel.prestamoCreated.collect { created ->
                if (created) {
                    Toast.makeText(requireContext(), "Préstamo guardado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnGuardar.setOnClickListener {
            val tipo = binding.etTipo.text.toString()
            val montoTotal = binding.etMontoTotal.text.toString().toDoubleOrNull()
            val valorCuota = binding.etValorCuota.text.toString().toDoubleOrNull()
            val numeroCuotas = binding.etNumeroCuotas.text.toString().toIntOrNull()
            val porcentajeInteres = binding.etPorcentajeInteres.text.toString().toDoubleOrNull()
            val fechaInicio = binding.etFechaInicio.text.toString()
            
            if (validateInputs(tipo, montoTotal, valorCuota, numeroCuotas, porcentajeInteres, fechaInicio)) {
                val prestamo = Prestamo(
                    clienteId = "temp_client_id", // This should come from navigation args
                    tipo = tipo,
                    montoTotal = montoTotal!!,
                    valorCuota = valorCuota!!,
                    numeroCuotas = numeroCuotas!!,
                    porcentajeInteres = porcentajeInteres!!,
                    fechaInicio = System.currentTimeMillis() // Should parse from fechaInicio string
                )
                viewModel.createPrestamo(prestamo)
            }
        }
        
        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun validateInputs(
        tipo: String,
        montoTotal: Double?,
        valorCuota: Double?,
        numeroCuotas: Int?,
        porcentajeInteres: Double?,
        fechaInicio: String
    ): Boolean {
        var isValid = true
        
        if (tipo.isBlank()) {
            binding.etTipo.error = "El tipo es requerido"
            isValid = false
        }
        
        if (montoTotal == null || montoTotal <= 0) {
            binding.etMontoTotal.error = "El monto debe ser mayor a 0"
            isValid = false
        }
        
        if (valorCuota == null || valorCuota <= 0) {
            binding.etValorCuota.error = "El valor de cuota debe ser mayor a 0"
            isValid = false
        }
        
        if (numeroCuotas == null || numeroCuotas <= 0) {
            binding.etNumeroCuotas.error = "El número de cuotas debe ser mayor a 0"
            isValid = false
        }
        
        if (porcentajeInteres == null || porcentajeInteres < 0) {
            binding.etPorcentajeInteres.error = "El porcentaje de interés debe ser mayor o igual a 0"
            isValid = false
        }
        
        if (fechaInicio.isBlank()) {
            binding.etFechaInicio.error = "La fecha de inicio es requerida"
            isValid = false
        }
        
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
