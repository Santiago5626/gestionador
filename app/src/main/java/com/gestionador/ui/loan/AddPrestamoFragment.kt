package com.gestionador.ui.loan

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
        updateFieldsVisibility(TipoPrestamo.DIARIO) // Por defecto
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
        
        // Listener para cambiar la visibilidad de campos según el tipo
        binding.spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val tipo = when (position) {
                    0 -> TipoPrestamo.DIARIO
                    1 -> TipoPrestamo.SEMANAL
                    2 -> TipoPrestamo.MENSUAL
                    else -> TipoPrestamo.DIARIO
                }
                updateFieldsVisibility(tipo)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun updateFieldsVisibility(tipo: TipoPrestamo) {
        when (tipo) {
            TipoPrestamo.MENSUAL -> {
                // Para préstamos mensuales: mostrar porcentaje de interés, ocultar valor de cuota
                binding.tilPorcentajeInteres.visibility = View.VISIBLE
                binding.tilValorCuota.visibility = View.GONE
                binding.tilPorcentajeInteres.hint = "Porcentaje de interés mensual (%)"
            }
            else -> {
                // Para préstamos diarios y semanales: mostrar valor de cuota, ocultar porcentaje
                binding.tilPorcentajeInteres.visibility = View.GONE
                binding.tilValorCuota.visibility = View.VISIBLE
            }
        }
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
            
            if (tipo == TipoPrestamo.MENSUAL) {
                // Para préstamos mensuales
                val porcentajeInteres = binding.etPorcentajeInteres.text.toString().toDoubleOrNull()
                
                if (validateInputsMensual(montoTotal, porcentajeInteres)) {
                    val prestamo = Prestamo(
                        clienteId = "temp_client_id", // This should come from navigation args
                        clienteNombre = "Cliente Temporal", // This should come from navigation args
                        tipo = tipo,
                        fechaInicial = selectedDate.timeInMillis,
                        montoTotal = montoTotal!!, // Capital inicial
                        valorCuotaPactada = 0.0, // No aplica para mensuales
                        numeroCuota = 1,
                        porcentajeInteres = porcentajeInteres!!,
                        saldoRestante = montoTotal, // Capital actual = capital inicial
                        interesesPendientes = 0.0, // Sin intereses pendientes al inicio
                        ultimaFechaCalculoInteres = selectedDate.timeInMillis
                    )
                    viewModel.createPrestamo(prestamo)
                }
            } else {
                // Para préstamos diarios y semanales
                val valorCuota = binding.etValorCuota.text.toString().toDoubleOrNull()
                
                if (validateInputsNormal(montoTotal, valorCuota)) {
                    val prestamo = Prestamo(
                        clienteId = "temp_client_id", // This should come from navigation args
                        clienteNombre = "Cliente Temporal", // This should come from navigation args
                        tipo = tipo,
                        fechaInicial = selectedDate.timeInMillis,
                        montoTotal = montoTotal!!,
                        valorCuotaPactada = valorCuota!!,
                        numeroCuota = 1,
                        porcentajeInteres = 0.0, // No aplica para diarios/semanales
                        saldoRestante = montoTotal,
                        interesesPendientes = 0.0,
                        ultimaFechaCalculoInteres = selectedDate.timeInMillis
                    )
                    viewModel.createPrestamo(prestamo)
                }
            }
        }
        
        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun validateInputsMensual(
        montoTotal: Double?,
        porcentajeInteres: Double?
    ): Boolean {
        var isValid = true
        
        if (montoTotal == null || montoTotal <= 0) {
            binding.etMontoTotal.error = "El monto debe ser mayor a 0"
            isValid = false
        }
        
        if (porcentajeInteres == null || porcentajeInteres <= 0) {
            binding.etPorcentajeInteres.error = "El porcentaje de interés debe ser mayor a 0"
            isValid = false
        }
        
        return isValid
    }
    
    private fun validateInputsNormal(
        montoTotal: Double?,
        valorCuota: Double?
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
        
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
