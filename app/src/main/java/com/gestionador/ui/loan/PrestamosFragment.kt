package com.gestionador.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gestionador.R
import com.gestionador.data.models.Prestamo
import com.gestionador.data.models.TipoPrestamo
import com.gestionador.databinding.FragmentPrestamosBinding
import kotlinx.coroutines.launch

class PrestamosFragment : Fragment() {

    private var _binding: FragmentPrestamosBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PrestamosViewModel by viewModels()
    private lateinit var adapter: PrestamosAdapter
    
    private var allPrestamos: List<Prestamo> = emptyList()
    private var currentFilter: TipoPrestamo? = null
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrestamosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupFilterChips()
        setupSearch()
        
        viewModel.loadPrestamos()
    }
    
    private fun setupRecyclerView() {
        adapter = PrestamosAdapter(
            onItemClick = { prestamo ->
                // Navigate to prestamo detail
                val bundle = bundleOf("prestamoId" to prestamo.id)
                findNavController().navigate(R.id.action_prestamosFragment_to_prestamoDetailFragment, bundle)
            },
            onEditClick = { prestamo ->
                // Navigate to edit prestamo (reusing AddPrestamoFragment)
                val bundle = bundleOf(
                    "prestamoId" to prestamo.id,
                    "isEdit" to true
                )
                findNavController().navigate(R.id.action_prestamosFragment_to_addPrestamoFragment, bundle)
            },
            onDeleteClick = { prestamo ->
                showDeleteConfirmationDialog(prestamo)
            },
            onAbonoClick = { prestamo ->
                // TODO: Navegar a pantalla de agregar abono
                Toast.makeText(requireContext(), "Funcionalidad de abono próximamente", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.rvPrestamos.apply {
            adapter = this@PrestamosFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.prestamos.collect { prestamos ->
                allPrestamos = prestamos
                applyCurrentFilter()
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
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_prestamosFragment_to_addPrestamoFragment)
        }
    }
    
    private fun setupFilterChips() {
        // Configurar chip "Todos" como seleccionado por defecto
        binding.chipTodos.isChecked = true
        
        binding.chipTodos.setOnClickListener {
            selectChip(binding.chipTodos)
            currentFilter = null
            applyCurrentFilter()
        }
        
        binding.chipDiarios.setOnClickListener {
            selectChip(binding.chipDiarios)
            currentFilter = TipoPrestamo.DIARIO
            applyCurrentFilter()
        }
        
        binding.chipSemanales.setOnClickListener {
            selectChip(binding.chipSemanales)
            currentFilter = TipoPrestamo.SEMANAL
            applyCurrentFilter()
        }
        
        binding.chipMensuales.setOnClickListener {
            selectChip(binding.chipMensuales)
            currentFilter = TipoPrestamo.MENSUAL
            applyCurrentFilter()
        }
    }
    
    private fun selectChip(selectedChip: com.google.android.material.chip.Chip) {
        // Deseleccionar todos los chips
        binding.chipTodos.isChecked = false
        binding.chipDiarios.isChecked = false
        binding.chipSemanales.isChecked = false
        binding.chipMensuales.isChecked = false
        
        // Seleccionar el chip clickeado
        selectedChip.isChecked = true
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            searchQuery = text.toString().trim()
            applyCurrentFilter()
        }
    }
    
    private fun applyCurrentFilter() {
        var filteredPrestamos = allPrestamos
        
        // Aplicar filtro por tipo
        if (currentFilter != null) {
            filteredPrestamos = filteredPrestamos.filter { it.tipo == currentFilter }
        }
        
        // Aplicar filtro de búsqueda por nombre de cliente
        if (searchQuery.isNotEmpty()) {
            filteredPrestamos = filteredPrestamos.filter { prestamo ->
                prestamo.clienteNombre.contains(searchQuery, ignoreCase = true)
            }
        }
        
        adapter.submitList(filteredPrestamos)
        binding.tvEmpty.visibility = if (filteredPrestamos.isEmpty()) View.VISIBLE else View.GONE
    }
    
    private fun showDeleteConfirmationDialog(prestamo: Prestamo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Préstamo")
            .setMessage("¿Está seguro que desea eliminar el préstamo de ${prestamo.clienteNombre} por $${String.format("%.2f", prestamo.montoTotal)}?\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deletePrestamo(prestamo.id)
                Toast.makeText(requireContext(), "Préstamo eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Recargar préstamos al volver a la pantalla
        viewModel.loadPrestamos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
