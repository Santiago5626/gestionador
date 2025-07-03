package com.gestionador.ui.loan

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gestionador.R
import com.gestionador.data.models.Abono
import com.gestionador.data.models.Prestamo
import com.gestionador.data.models.TipoPrestamo
import com.gestionador.databinding.FragmentPrestamoDetailBinding
import com.gestionador.ui.loan.PrestamosViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PrestamoDetailFragment : Fragment() {

    private var _binding: FragmentPrestamoDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PrestamosViewModel by viewModels()
    private var prestamoId: String? = null
    private var currentPrestamo: Prestamo? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prestamoId = arguments?.getString("prestamoId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrestamoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
        loadPrestamoData()
        // loadAbonosData() removed to call it after currentPrestamo is set
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.prestamos.collect { prestamos ->
                val prestamo = prestamos.find { it.id == prestamoId }
                prestamo?.let {
                    currentPrestamo = it
                    displayPrestamoData(it)
                    loadAbonosData()  // Call loadAbonosData after currentPrestamo is set
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnEditar.setOnClickListener {
            currentPrestamo?.let { prestamo ->
                val bundle = Bundle().apply {
                    putString("prestamoId", prestamo.id)
                    putBoolean("isEdit", true)
                }
                findNavController().navigate(R.id.action_prestamoDetailFragment_to_addPrestamoFragment, bundle)
            }
        }
        
        binding.btnEliminar.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        
        binding.btnAbonar.setOnClickListener {
            showAbonoDialog()
        }

        binding.btnVerCarton.setOnClickListener {
            currentPrestamo?.let { prestamo ->
                val bundle = Bundle().apply {
                    putString("prestamoId", prestamo.id)
                    putString("clienteNombre", prestamo.clienteNombre)
                    putDouble("montoTotal", prestamo.montoTotal)
                    putLong("fechaInicial", prestamo.fechaInicial)
                }
                // Cambiar la acción de navegación a la acción correcta definida en nav_graph.xml
                findNavController().navigate(R.id.action_prestamoDetailFragment_to_prestamoCartonFragment, bundle)
            }
        }
    }
    
    private fun loadPrestamoData() {
        viewModel.loadPrestamos()
    }
    
    private fun loadAbonosData() {
        currentPrestamo?.let { prestamo ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.obtenerAbonos(prestamo.id).collect { abonos: List<Abono> ->
                    // Actualizar saldo restante en el préstamo con base en abonos
                    val valorADevolver = if (prestamo.tipo == TipoPrestamo.MENSUAL) {
                        prestamo.saldoRestante + prestamo.interesesPendientes
                    } else {
                        prestamo.valorDevolver
                    }
                    val sumaAbonos = abonos.sumOf { it.montoAbonado }
                    val saldoRestanteCalculado = valorADevolver - sumaAbonos
                    currentPrestamo = prestamo.copy(
                        saldoRestante = saldoRestanteCalculado,
                        valorDevolver = saldoRestanteCalculado // Actualizar valorDevolver para que refleje el saldo restante
                    )
                    displayAbonosData(abonos, currentPrestamo!!)
                }
            }
        }
    }
    
    private fun displayPrestamoData(prestamo: Prestamo) {
        binding.tvClienteNombre.text = prestamo.clienteNombre
        binding.tvFechaCreacion.text = "Creado el ${dateFormat.format(Date(prestamo.fechaInicial))}"

        // Formatear monto total solo con el valor, sin texto adicional
        val montoTotalFormatted = String.format("%,.0f", prestamo.montoTotal)
        if (prestamo.tipo == TipoPrestamo.MENSUAL) {
            binding.tvMontoTotal.text = "$montoTotalFormatted"
        } else {
            binding.tvMontoTotal.text = "$$montoTotalFormatted"
        }

        // Tipo de préstamo
        val tipoText = when (prestamo.tipo) {
            TipoPrestamo.DIARIO -> "Diario"
            TipoPrestamo.SEMANAL -> "Semanal"
            TipoPrestamo.MENSUAL -> "Mensual"
        }
        binding.tvTipoPrestamo.text = tipoText

        // Mostrar saldo restante e interés también aquí para reflejar pagos
        val saldoRestanteFormatted = String.format("%,.0f", prestamo.saldoRestante)
        binding.tvSaldoRestante.text = "$$saldoRestanteFormatted"

        val interesFormatted = String.format("%.2f", prestamo.interesesPendientes)
        binding.tvInteres.text = "$interesFormatted %"
    }

    private lateinit var cartonAdapter: PrestamoCartonAdapter

    private fun displayAbonosData(abonos: List<Abono>, prestamo: Prestamo) {
        if (!::cartonAdapter.isInitialized) {
            cartonAdapter = PrestamoCartonAdapter()
            binding.rvCarton.adapter = cartonAdapter
        }
        cartonAdapter.submitList(abonos)

        // Always use prestamo.saldoRestante for saldo restante display
        val saldoRestanteFinal = prestamo.saldoRestante

        // Calcular interés como porcentaje fijo para todos los tipos, sin cambiar con abonos
        val porcentajeInteres = if (prestamo.montoTotal != 0.0) {
            // Aplicar fórmula: ((P - M) / M) * 100
            ((prestamo.valorDevolver - prestamo.montoTotal) / prestamo.montoTotal) * 100
        } else {
            0.0
        }

        // For monthly loans, ensure interest does not change with abonos
        if (prestamo.tipo == TipoPrestamo.MENSUAL) {
            // Interest is fixed and should not be recalculated based on abonos
            // So we keep porcentajeInteres as calculated above without modification
        }

        // Formatear valores
        val saldoRestanteFormatted = String.format("%,.0f", saldoRestanteFinal)
        val interesFormatted = String.format("%.2f", porcentajeInteres)

        // Actualizar UI con saldo restante e interés sin la palabra "de interés"
        binding.tvSaldoRestante.text = "$$saldoRestanteFormatted"
        binding.tvInteres.text = "$interesFormatted %"
    }
    
    private fun showDeleteConfirmationDialog() {
        currentPrestamo?.let { prestamo ->
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Préstamo")
                .setMessage("¿Está seguro de que desea eliminar el préstamo de ${prestamo.clienteNombre}?")
                .setPositiveButton("Eliminar") { _, _ ->
                    deletePrestamo(prestamo.id)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
    
    private fun showAbonoDialog() {
        currentPrestamo?.let { prestamo ->
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_abono, null)
            val etMonto = dialogView.findViewById<EditText>(R.id.etMontoAbono)
            
            AlertDialog.Builder(requireContext())
                .setTitle("Realizar Abono")
                .setView(dialogView)
                .setPositiveButton("Abonar") { _, _ ->
                    val montoText = etMonto.text.toString()
                    val monto = montoText.toDoubleOrNull()
                    
                    if (monto != null && monto > 0) {
                        if (monto <= prestamo.saldoRestante) {
                            realizarAbono(prestamo, monto)
                        } else {
                            Toast.makeText(requireContext(), "El monto no puede ser mayor al saldo restante", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
    
    private fun deletePrestamo(prestamoId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Primero eliminar los abonos relacionados al préstamo
                viewModel.obtenerAbonos(prestamoId).collect { abonos ->
                    abonos.forEach { abono ->
                        viewModel.eliminarAbono(abono.id)
                    }
                }
                // Luego eliminar el préstamo
                viewModel.deletePrestamo(prestamoId)
                Toast.makeText(requireContext(), "Préstamo y abonos eliminados exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al eliminar el préstamo y abonos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun realizarAbono(prestamo: Prestamo, monto: Double) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val abono = Abono.crearAbonoNormal(
                    prestamoId = prestamo.id,
                    numeroCuota = prestamo.numeroCuota,
                    montoAbonado = monto,
                    saldoRestante = prestamo.saldoRestante - monto,
                    valorCuotaPactada = prestamo.saldoRestante // Cambiado para usar saldoRestante en lugar de valorDevolver
                )
                
                // Actualizar el préstamo
                val prestamoActualizado = prestamo.copy(
                    saldoRestante = (prestamo.saldoRestante - monto).coerceAtLeast(0.0),
                    numeroCuota = prestamo.numeroCuota + 1
                )
                
                // Guardar el préstamo actualizado en la base de datos
                // Removed call to actualizarPrestamo as it does not exist
                // viewModel.actualizarPrestamo(prestamoActualizado)
                
                viewModel.realizarAbono(abono, prestamoActualizado)
                Toast.makeText(requireContext(), "Abono realizado exitosamente", Toast.LENGTH_SHORT).show()
                
                // Actualizar la vista
                displayPrestamoData(prestamoActualizado)
                currentPrestamo = prestamoActualizado

                // Reload abonos data to update saldo restante UI
                loadAbonosData()
                
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al realizar el abono: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
