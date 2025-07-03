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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gestionador.databinding.FragmentPrestamoCartonBinding
import kotlinx.coroutines.launch

class PrestamoCartonFragment : Fragment() {

    private var _binding: FragmentPrestamoCartonBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PrestamosViewModel by viewModels()
    private val args: PrestamoCartonFragmentArgs by navArgs()

    private lateinit var adapter: PrestamoCartonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrestamoCartonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupRecyclerView()
        loadCartonData()
    }

    private fun setupHeader() {
        val clienteNombre = arguments?.getString("clienteNombre") ?: "N/A"
        val montoTotal = arguments?.getDouble("montoTotal") ?: 0.0
        val fechaInicial = arguments?.getLong("fechaInicial") ?: 0L

        binding.tvClienteNombre.text = clienteNombre
        binding.tvMontoPrestado.text = String.format("$%,.0f", montoTotal)
        if (fechaInicial != 0L) {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            binding.tvFechaPrestamo.text = "Fecha del préstamo: ${sdf.format(java.util.Date(fechaInicial))}"
        } else {
            binding.tvFechaPrestamo.text = "Fecha del préstamo: N/A"
        }
    }

    private fun loadCartonData() {
        val prestamoId = arguments?.getString("prestamoId")
        val clienteNombre = arguments?.getString("clienteNombre")
        val montoTotal = arguments?.getDouble("montoTotal") ?: 0.0
        val valorDevolver = arguments?.getDouble("valorDevolver") ?: montoTotal
        val fechaInicial = arguments?.getLong("fechaInicial") ?: 0L

        if (prestamoId == null || clienteNombre == null || fechaInicial == 0L) {
            Toast.makeText(requireContext(), "Datos de préstamo incompletos", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        lifecycleScope.launch {
            viewModel.obtenerAbonos(prestamoId).collect { abonos ->

                if (abonos.isEmpty()) {
                    // No mostrar la tabla si no hay abonos, solo mostrar datos del préstamo
                    binding.rvCarton.visibility = View.GONE
                } else {
                    binding.rvCarton.visibility = View.VISIBLE
                    adapter.submitList(abonos)
                }

                // Calcular saldo restante y mostrar
                val sumaAbonos = abonos.sumOf { it.montoAbonado }
                val saldoRestante = valorDevolver - sumaAbonos
                binding.tvSaldoRestante.text = String.format("Saldo Restante: $%,.0f", saldoRestante)

                // Calcular interés como porcentaje fijo basado en valorDevolver y montoTotal
                val interes = if (montoTotal != 0.0) {
                    ((valorDevolver - montoTotal) / montoTotal) * 100
                } else {
                    0.0
                }
                binding.tvInteres.text = String.format("Interés: %.2f%%", interes)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PrestamoCartonAdapter()
        binding.rvCarton.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCarton.adapter = adapter
    }

    private fun loadCartonData() {
        val prestamoId = arguments?.getString("prestamoId")
        val clienteNombre = arguments?.getString("clienteNombre")
        val montoTotal = arguments?.getDouble("montoTotal") ?: 0.0
        val fechaInicial = arguments?.getLong("fechaInicial") ?: 0L

        if (prestamoId == null || clienteNombre == null || fechaInicial == 0L) {
            Toast.makeText(requireContext(), "Datos de préstamo incompletos", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        lifecycleScope.launch {
            viewModel.obtenerAbonos(prestamoId).collect { abonos ->

                if (abonos.isEmpty()) {
                    // No mostrar la tabla si no hay abonos, solo mostrar datos del préstamo
                    binding.rvCarton.visibility = View.GONE
                } else {
                    binding.rvCarton.visibility = View.VISIBLE
                    adapter.submitList(abonos)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
