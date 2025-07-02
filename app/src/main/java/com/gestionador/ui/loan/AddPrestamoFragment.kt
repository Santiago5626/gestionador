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
import com.gestionador.data.models.Cliente
import com.gestionador.data.models.Prestamo
import com.gestionador.data.models.TipoPrestamo
import com.gestionador.databinding.FragmentAddPrestamoBinding
import com.gestionador.ui.client.ClientesViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddPrestamoFragment : Fragment() {

    private var _binding: FragmentAddPrestamoBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddPrestamoViewModel by viewModels()
    private val clientesViewModel: ClientesViewModel by viewModels()
    private val prestamosViewModel: PrestamosViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDate = Calendar.getInstance()
    
    private var clientes: List<Cliente> = emptyList()
    private var selectedCliente: Cliente? = null
    
    private var prestamoId: String? = null
    private var isEdit: Boolean = false
    private var currentPrestamo: Prestamo? = null

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
        
        // Obtener argumentos
        prestamoId = arguments?.getString("prestamoId")
        isEdit = arguments?.getBoolean("isEdit", false) ?: false
        
        setupUI()
        setupSpinners()
        setupDatePicker()
        setupObservers()
        setupClickListeners()
        updateFieldsVisibility(TipoPrestamo.DIARIO) // Por defecto
        
        // Cargar clientes
        clientesViewModel.loadClientes()
        
        if (isEdit && prestamoId != null) {
            loadPrestamoData()
        }
    }
    
    private fun setupUI() {
        // Cambiar título según el modo
        binding.tvTitle.text = if (isEdit) "Editar Préstamo" else "Agregar Préstamo"
        binding.btnGuardar.text = if (isEdit) "Actualizar" else "Guardar"
    }
    
    private fun loadPrestamoData() {
        prestamosViewModel.loadPrestamos()
    }
    
    private fun setupSpinners() {
        // Spinner de tipos de préstamo
        val tipos = listOf("Diario", "Semanal", "Mensual")
        val tipoAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tipos
        )
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = tipoAdapter
        
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
        
        // Listener para selección de cliente
        binding.spinnerCliente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && clientes.isNotEmpty()) { // Posición 0 es "Seleccionar cliente"
                    selectedCliente = clientes[position - 1]
                } else {
                    selectedCliente = null
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCliente = null
            }
        }
    }
    
    private fun updateFieldsVisibility(tipo: TipoPrestamo) {
        when (tipo) {
            TipoPrestamo.MENSUAL -> {
                // Para préstamos mensuales: mostrar porcentaje de interés, ocultar valor de cuota
                binding.tilPorcentajeInteres.visibility = View.VISIBLE
                binding.tilValorCuota.visibility = View.GONE
                binding.tilPorcentajeInteres.hint = "Porcentaje de interés mensual (%)"
                binding.tilMontoTotal.hint = "Monto a prestar"
            }
            else -> {
                // Para préstamos diarios y semanales: mostrar valor a devolver, ocultar porcentaje
                binding.tilPorcentajeInteres.visibility = View.GONE
                binding.tilValorCuota.visibility = View.VISIBLE
                binding.tilValorCuota.hint = "Valor a devolver"
                binding.tilMontoTotal.hint = "Monto a prestar"
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
        // Observar clientes
        viewLifecycleOwner.lifecycleScope.launch {
            clientesViewModel.clientes.collect { clientesList ->
                clientes = clientesList
                setupClienteSpinner()
                
                // Si estamos en modo edición y ya tenemos el préstamo cargado, seleccionar el cliente
                currentPrestamo?.let { prestamo ->
                    selectClienteInSpinner(prestamo.clienteId)
                }
            }
        }
        
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
                    val message = if (isEdit) "Préstamo actualizado exitosamente" else "Préstamo guardado exitosamente"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
        
        // Observer para cargar datos del préstamo en modo edición
        if (isEdit) {
            viewLifecycleOwner.lifecycleScope.launch {
                prestamosViewModel.prestamos.collect { prestamos ->
                    val prestamo = prestamos.find { it.id == prestamoId }
                    prestamo?.let {
                        currentPrestamo = it
                        fillPrestamoData(it)
                        
                        // Si ya tenemos los clientes cargados, seleccionar el cliente
                        if (clientes.isNotEmpty()) {
                            selectClienteInSpinner(it.clienteId)
                        }
                    }
                }
            }
        }
    }
    
    private fun fillPrestamoData(prestamo: Prestamo) {
        // Llenar campos básicos
        binding.etMontoTotal.setText(prestamo.montoTotal.toString())
        
        // Configurar fecha
        selectedDate.timeInMillis = prestamo.fechaInicial
        binding.etFechaInicio.setText(dateFormat.format(selectedDate.time))
        
        // Configurar tipo de préstamo
        val tipoIndex = when (prestamo.tipo) {
            TipoPrestamo.DIARIO -> 0
            TipoPrestamo.SEMANAL -> 1
            TipoPrestamo.MENSUAL -> 2
        }
        binding.spinnerTipo.setSelection(tipoIndex)
        
        // Configurar campos específicos según el tipo
        if (prestamo.tipo == TipoPrestamo.MENSUAL) {
            binding.etPorcentajeInteres.setText(prestamo.porcentajeInteres.toString())
        } else {
            binding.etValorCuota.setText(prestamo.valorCuotaPactada.toString())
        }
        
        // Seleccionar cliente
        val clienteIndex = clientes.indexOfFirst { it.id == prestamo.clienteId }
        if (clienteIndex >= 0) {
            binding.spinnerCliente.setSelection(clienteIndex + 1) // +1 porque el primer item es "Seleccionar cliente"
            selectedCliente = clientes[clienteIndex]
        }
    }
    
    private fun setupClienteSpinner() {
        val clienteNames = mutableListOf("Seleccionar cliente")
        clienteNames.addAll(clientes.map { it.getNombreCompleto() })
        
        val clienteAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            clienteNames
        )
        clienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCliente.adapter = clienteAdapter
    }
    
    private fun selectClienteInSpinner(clienteId: String) {
        val clienteIndex = clientes.indexOfFirst { it.id == clienteId }
        if (clienteIndex >= 0) {
            binding.spinnerCliente.setSelection(clienteIndex + 1) // +1 porque el primer item es "Seleccionar cliente"
            selectedCliente = clientes[clienteIndex]
        }
    }
    
    private fun setupClickListeners() {
        binding.btnGuardar.setOnClickListener {
            if (selectedCliente == null) {
                Toast.makeText(requireContext(), "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
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
                    val prestamo = if (isEdit && currentPrestamo != null) {
                        // Actualizar préstamo existente
                        currentPrestamo!!.copy(
                            clienteId = selectedCliente!!.id,
                            clienteNombre = selectedCliente!!.getNombreCompleto(),
                            tipo = tipo,
                            fechaInicial = selectedDate.timeInMillis,
                            montoTotal = montoTotal!!,
                            valorCuotaPactada = 0.0,
                            porcentajeInteres = porcentajeInteres!!,
                            saldoRestante = montoTotal + (montoTotal * porcentajeInteres / 100) // Recalcular saldo restante al editar
                        )
                    } else {
                        // Crear nuevo préstamo
                        Prestamo(
                            clienteId = selectedCliente!!.id,
                            clienteNombre = selectedCliente!!.getNombreCompleto(),
                            tipo = tipo,
                            fechaInicial = selectedDate.timeInMillis,
                            montoTotal = montoTotal!!, // Capital inicial
                            valorCuotaPactada = 0.0, // No aplica para mensuales
                            numeroCuota = 1,
                            porcentajeInteres = porcentajeInteres!!,
                            saldoRestante = montoTotal + (montoTotal * porcentajeInteres / 100), // Capital + interés inicial
                            interesesPendientes = 0.0, // Sin intereses pendientes al inicio
                            ultimaFechaCalculoInteres = selectedDate.timeInMillis
                        )
                    }
                    viewModel.createPrestamo(prestamo)
                }
            } else {
                // Para préstamos diarios y semanales
                val valorDevolver = binding.etValorCuota.text.toString().toDoubleOrNull()
                
                if (validateInputsNormal(montoTotal, valorDevolver)) {
                    val prestamo = if (isEdit && currentPrestamo != null) {
                        // Actualizar préstamo existente
                        currentPrestamo!!.copy(
                            clienteId = selectedCliente!!.id,
                            clienteNombre = selectedCliente!!.getNombreCompleto(),
                            tipo = tipo,
                            fechaInicial = selectedDate.timeInMillis,
                            montoTotal = montoTotal!!,
                            valorCuotaPactada = valorDevolver!!,
                            porcentajeInteres = 0.0
                        )
                    } else {
                        // Crear nuevo préstamo
                    Prestamo(
                        clienteId = selectedCliente!!.id,
                        clienteNombre = selectedCliente!!.getNombreCompleto(),
                        tipo = tipo,
                        fechaInicial = selectedDate.timeInMillis,
                        montoTotal = montoTotal!!,
                        valorCuotaPactada = valorDevolver!!, // Usar valor a devolver para diarios/semanales
                        numeroCuota = 1,
                        porcentajeInteres = 0.0, // No aplica para diarios/semanales
                        saldoRestante = montoTotal,
                        interesesPendientes = 0.0,
                        ultimaFechaCalculoInteres = selectedDate.timeInMillis
                    )
                    }
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
