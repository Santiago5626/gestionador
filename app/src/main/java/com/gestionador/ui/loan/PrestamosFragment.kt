package com.gestionador.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gestionador.R
import com.gestionador.data.models.Prestamo
import com.gestionador.databinding.FragmentPrestamosBinding
import kotlinx.coroutines.launch

class PrestamosFragment : Fragment() {

    private var _binding: FragmentPrestamosBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PrestamosViewModel by viewModels()
    private lateinit var adapter: PrestamosAdapter

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
                adapter.submitList(prestamos)
                binding.tvEmpty.visibility = if (prestamos.isEmpty()) View.VISIBLE else View.GONE
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
