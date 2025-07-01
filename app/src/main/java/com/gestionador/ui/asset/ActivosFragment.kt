package com.gestionador.ui.asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gestionador.R
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
    
    private fun setupRecyclerView() {
        activosAdapter = ActivosAdapter()
        
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
    }
    
    private fun setupClickListeners() {
        binding.fabAddActivo.setOnClickListener {
            findNavController().navigate(R.id.action_activosFragment_to_addActivoFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
