package com.gestionador.ui.loan

import android.app.DatePickerDialog
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
import com.gestionador.data.models.Prestamo
import com.gestionador.data.models.TipoPrestamo
import com.gestionador.databinding.FragmentAddPrestamoBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddPrestamoFragment : Fragment() {

    private var _binding: FragmentAddPrestamoBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddPrestamoViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDate = Calendar.getInstance()

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
        
        setupSpinner()
        setupDatePicker()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupSpinner() {
        val tipos = listOf("Diario", "Semanal", "Mensual")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tipos
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapter
    }

    private fun setupDatePicker() {
        // Establecer la fecha actual por defecto
        binding.etFechaInicio.setText(dateFormat.format(selectedDate.time))

        // Configurar el click listener para mostrar el DatePickerDialog
        binding.etFechaInicio.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    binding.etFechaInicio.setText(dateFormat.format(selectedDate.time))
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
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
            val selectedIndex = binding.spinnerTipo.selectedItemPosition
            val tipo = when (selectedIndex) {
                0 -> TipoPrestamo.DIARIO
                1 -> TipoPrestamo.SEMANAL
                2 -> TipoPrestamo.MENSUAL
                else -> TipoPrestamo.DIARIO
            }
            
            val montoTotal = binding.etMontoTotal.text.toString().toDoubleOrNull()
            val valorCuota = binding.etValorCuota.text.toString().toDoubleOrNull()
            val numeroCuotas = binding.etNumeroCuotas.text.toString().toIntOrNull()
            val porcentajeInteres = binding.etPorcentajeInteres.text.toString().toDoubleOrNull() ?: 0.0
            
            if (validateInputs(montoTotal, valorCuota, numeroCuotas)) {
                val prestamo = Prestamo(
                    clienteId = "temp_client_id", // This should come from navigation args
                    clienteNombre = "Cliente Temporal", // This should come from navigation args
                    tipo = tipo,
                    fechaInicial = selectedDate.timeInMillis,
                    montoTotal = montoTotal!!,
                    valorCuotaPactada = valorCuota!!,
                    numeroCuota = numeroCuotas!!,
                    porcentajeInteres = porcentajeInteres,
                    saldoRestante = montoTotal
                )
                viewModel.createPrestamo(prestamo)
            }
        }
        
        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun validateInputs(
        montoTotal: Double?,
        valorCuota: Double?,
        numeroCuotas: Int?
    ): Boolean {
        var isValid = true
        
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
        
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
