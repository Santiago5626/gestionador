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
import androidx.recyclerview.widget.LinearLayoutManager
import com.gestionador.databinding.FragmentPrestamoCartonBinding
import kotlinx.coroutines.launch

class PrestamoCartonFragment : Fragment() {

    private var _binding: FragmentPrestamoCartonBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PrestamosViewModel by viewModels()

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

        setupRecyclerView()
        loadCartonData()
    }

    private fun setupHeader(prestamo: com.gestionador.data.models.Prestamo) {
        binding.tvClienteNombre.text = prestamo.clienteNombre
        binding.tvMontoPrestado.text = String.format("$%,.0f", prestamo.montoTotal)
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        binding.tvFechaPrestamo.text = "Fecha del préstamo: ${sdf.format(java.util.Date(prestamo.fechaInicial))}"
    }

    private fun loadCartonData() {
        val prestamoId = arguments?.getString("prestamoId")
        if (prestamoId == null) {
            Toast.makeText(requireContext(), "Datos de préstamo incompletos", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        lifecycleScope.launch {
            val prestamos = viewModel.prestamos.value
            val prestamo = prestamos.find { it.id == prestamoId }
            if (prestamo == null) {
                Toast.makeText(requireContext(), "Préstamo no encontrado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
                return@launch
            }
            setupHeader(prestamo)

            viewModel.obtenerAbonos(prestamoId).collect { abonos ->
                if (abonos.isEmpty()) {
                    binding.rvCarton.visibility = View.GONE
                } else {
                    binding.rvCarton.visibility = View.VISIBLE
                    adapter.submitList(abonos)
                }

                binding.tvSaldoRestante.text = String.format("Saldo Restante: $%,.0f", prestamo.saldoRestante)

                val interes = if (prestamo.montoTotal != 0.0) {
                    ((prestamo.valorDevolver - prestamo.montoTotal) / prestamo.montoTotal) * 100
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
