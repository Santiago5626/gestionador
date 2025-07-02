package com.gestionador.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        adapter = PrestamosAdapter { prestamo ->
            // Navigate to prestamo detail
        }
        
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
    }
    
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            // Navigate to add prestamo
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
