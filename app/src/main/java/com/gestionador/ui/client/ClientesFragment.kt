package com.gestionador.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gestionador.R
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
        clientesAdapter = ClientesAdapter { cliente ->
            // Navigate to cliente detail
            val bundle = bundleOf("clienteId" to cliente.id)
            findNavController().navigate(R.id.action_clientesFragment_to_clienteDetailFragment, bundle)
        }
        
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
    }
    
    private fun setupClickListeners() {
        binding.fabAddCliente.setOnClickListener {
            findNavController().navigate(R.id.action_clientesFragment_to_addClienteFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
