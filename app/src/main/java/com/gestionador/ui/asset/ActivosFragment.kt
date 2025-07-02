package com.gestionador.ui.asset

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
import com.gestionador.data.models.Activo
import com.gestionador.databinding.FragmentActivosBinding
import kotlinx.coroutines.launch

class ActivosFragment : Fragment() {
    
    private var _binding: FragmentActivosBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ActivosViewModel by viewModels()
    private lateinit var activosAdapter: ActivosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        // Load activos
        viewModel.loadActivos()
    }

    override fun onResume() {
        super.onResume()
        // Recargar activos cuando el usuario regrese a este fragmento
        viewModel.loadActivos()
    }
    
    private fun setupRecyclerView() {
        activosAdapter = ActivosAdapter(
            onEditClick = { activo ->
                // Navigate to edit activo (reusing AddActivoFragment)
                val bundle = bundleOf(
                    "activoId" to activo.id,
                    "isEdit" to true
                )
                findNavController().navigate(R.id.action_activosFragment_to_addActivoFragment, bundle)
            },
            onDeleteClick = { activo ->
                showDeleteConfirmationDialog(activo)
            }
        )
        
        binding.recyclerViewActivos.apply {
            adapter = activosAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activos.collect { activos ->
                activosAdapter.submitList(activos)
                binding.emptyView.visibility = if (activos.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalBalance.collect { balance ->
                binding.textViewBalance.text = "Balance Total: $${String.format("%.2f", balance)}"
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
        binding.fabAddActivo.setOnClickListener {
            findNavController().navigate(R.id.action_activosFragment_to_addActivoFragment)
        }
    }
    
    private fun showDeleteConfirmationDialog(activo: Activo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Activo")
            .setMessage("¿Está seguro que desea eliminar este activo por ${String.format("$%.2f", activo.monto)}?\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteActivo(activo.id)
                Toast.makeText(requireContext(), "Activo eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
