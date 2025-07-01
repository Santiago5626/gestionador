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
import com.gestionador.R
import com.gestionador.databinding.FragmentPrestamosBinding
import kotlinx.coroutines.launch

class PrestamosFragment : Fragment() {
    
    private var _binding: FragmentPrestamosBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PrestamosViewModel by viewModels()
    private lateinit var prestamosAdapter: PrestamosAdapter

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
        
        // Load prestamos
        viewModel.loadPrestamos()
    }
    
    private fun setupRecyclerView() {
        prestamosAdapter = PrestamosAdapter { prestamo ->
            // Navigate to prestamo detail
            val action = PrestamosFragmentDirections
                .actionPrestamosFragmentToPrestamoDetailFragment(prestamo.id)
            findNavController().navigate(action)
        }
        
        binding.recyclerViewPrestamos.apply {
            adapter = prestamosAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.prestamos.collect { prestamos ->
                prestamosAdapter.submitList(prestamos)
                binding.emptyView.visibility = if (prestamos.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddPrestamo.setOnClickListener {
            findNavController().navigate(R.id.action_prestamosFragment_to_addPrestamoFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
