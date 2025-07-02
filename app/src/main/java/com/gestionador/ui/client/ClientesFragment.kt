package com.gestionador.ui.client

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
import com.gestionador.data.models.Cliente
import com.gestionador.databinding.FragmentClientesBinding
import kotlinx.coroutines.launch

class ClientesFragment : Fragment() {
    
    private var _binding: FragmentClientesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ClientesViewModel by viewModels()
    private lateinit var clientesAdapter: ClientesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        // Load clientes
        viewModel.loadClientes()
    }
    
    private fun setupRecyclerView() {
        clientesAdapter = ClientesAdapter(
            onClienteClick = { cliente ->
                // Navigate to cliente detail
                val bundle = bundleOf("clienteId" to cliente.id)
                findNavController().navigate(R.id.action_clientesFragment_to_clienteDetailFragment, bundle)
            },
            onEditClick = { cliente ->
                // Navigate to edit cliente (reusing AddClienteFragment)
                val bundle = bundleOf(
                    "clienteId" to cliente.id,
                    "isEdit" to true
                )
                findNavController().navigate(R.id.action_clientesFragment_to_addClienteFragment, bundle)
            },
            onDeleteClick = { cliente ->
                showDeleteConfirmationDialog(cliente)
            }
        )
        
        binding.recyclerViewClientes.apply {
            adapter = clientesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.clientes.collect { clientes ->
                clientesAdapter.submitList(clientes)
                binding.emptyView.visibility = if (clientes.isEmpty()) View.VISIBLE else View.GONE
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
        binding.fabAddCliente.setOnClickListener {
            findNavController().navigate(R.id.action_clientesFragment_to_addClienteFragment)
        }
    }
    
    private fun showDeleteConfirmationDialog(cliente: Cliente) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Cliente")
            .setMessage("¿Está seguro que desea eliminar a ${cliente.getNombreCompleto()}?\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteCliente(cliente.id)
                Toast.makeText(requireContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Recargar clientes al volver a la pantalla
        viewModel.loadClientes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
