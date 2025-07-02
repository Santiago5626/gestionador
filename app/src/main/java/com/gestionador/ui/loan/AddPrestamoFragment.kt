package com.gestionador.ui.loan

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
import com.gestionador.R
import com.gestionador.data.models.Prestamo
import com.gestionador.data.models.TipoPrestamo
import com.gestionador.data.models.EstadoPrestamo
import com.gestionador.data.repository.FirebaseRepository
import com.gestionador.databinding.FragmentAddPrestamoBinding
import kotlinx.coroutines.launch

class AddPrestamoFragment : Fragment() {

    private var _binding: FragmentAddPrestamoBinding? = null
    private val binding get() = _binding!!
    
    private var clienteId: String? = null
    private var clienteNombre: String? = null
    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clienteId = arguments?.getString("clienteId")
    }

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
        
        setupUI()
        setupListeners()
        
        // Si viene de un cliente específico, cargar sus datos
        clienteId?.let { loadClienteData(it) }
    }

    private fun setupUI() {
        // Configurar spinner de tipo de préstamo
        val tiposAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Diario", "Semanal", "Mensual")
        )
        tiposAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = tiposAdapter

        // Configurar spinner de clientes si no viene de un cliente específico
        if (clienteId == null) {
            loadClientes()
        } else {
            binding.tilCliente.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnGuardar.setOnClickListener {
            if (validateInput()) {
                savePrestamo()
            }
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadClientes() {
        lifecycleScope.launch {
            repository.getClientes()
                .onSuccess { clientes ->
                    val clientesNames = clientes.map { "${it.nombre} ${it.apellido}" }
                    val clientesAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        clientesNames
                    )
                    clientesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerCliente.adapter = clientesAdapter
                }
                .onFailure {
                    Toast.makeText(context, "Error al cargar clientes", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadClienteData(id: String) {
        lifecycleScope.launch {
            repository.getCliente(id)
                .onSuccess { cliente ->
                    clienteNombre = "${cliente.nombre} ${cliente.apellido}"
                    binding.tvClienteSeleccionado.text = clienteNombre
                    binding.tvClienteSeleccionado.visibility = View.VISIBLE
                }
                .onFailure {
                    Toast.makeText(context, "Error al cargar cliente", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (binding.etMontoTotal.text.toString().isEmpty()) {
            binding.tilMontoTotal.error = "Ingrese el monto total"
            isValid = false
        } else {
            binding.tilMontoTotal.error = null
        }

        if (binding.etValorCuota.text.toString().isEmpty()) {
            binding.tilValorCuota.error = "Ingrese el valor de la cuota"
            isValid = false
        } else {
            binding.tilValorCuota.error = null
        }

        if (binding.etNumeroCuotas.text.toString().isEmpty()) {
            binding.tilNumeroCuotas.error = "Ingrese el número de cuotas"
            isValid = false
        } else {
            binding.tilNumeroCuotas.error = null
        }

        // Validar porcentaje de interés para préstamos mensuales
        val tipoSeleccionado = binding.spinnerTipo.selectedItemPosition
        if (tipoSeleccionado == 2 && binding.etPorcentajeInteres.text.toString().isEmpty()) { // Mensual
            binding.tilPorcentajeInteres.error = "Ingrese el porcentaje de interés"
            isValid = false
        } else {
            binding.tilPorcentajeInteres.error = null
        }

        return isValid
    }

    private fun savePrestamo() {
        binding.btnGuardar.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        val montoTotal = binding.etMontoTotal.text.toString().toDouble()
        val valorCuota = binding.etValorCuota.text.toString().toDouble()
        val numeroCuotas = binding.etNumeroCuotas.text.toString().toInt()
        val porcentajeInteres = if (binding.etPorcentajeInteres.text.toString().isNotEmpty()) {
            binding.etPorcentajeInteres.text.toString().toDouble()
        } else 0.0

        val tipo = when (binding.spinnerTipo.selectedItemPosition) {
            0 -> TipoPrestamo.DIARIO
            1 -> TipoPrestamo.SEMANAL
            2 -> TipoPrestamo.MENSUAL
            else -> TipoPrestamo.DIARIO
        }

        val prestamo = Prestamo(
            id = "", // Firebase generará el ID
            clienteId = clienteId ?: "", // TODO: Obtener del spinner si no viene de cliente
            clienteNombre = clienteNombre ?: "", // TODO: Obtener del spinner si no viene de cliente
            tipo = tipo,
            fechaInicial = System.currentTimeMillis(),
            numeroCuota = numeroCuotas,
            valorCuotaPactada = valorCuota,
            porcentajeInteres = porcentajeInteres,
            montoTotal = montoTotal,
            saldoRestante = montoTotal,
            estado = EstadoPrestamo.ACTIVO,
            fechaCreacion = System.currentTimeMillis()
        )

        lifecycleScope.launch {
            repository.addPrestamo(prestamo)
                .onSuccess {
                    Toast.makeText(context, "Préstamo creado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .onFailure { exception ->
                    Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                    binding.btnGuardar.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
