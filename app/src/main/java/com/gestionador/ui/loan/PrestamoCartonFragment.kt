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
import com.gestionador.ui.loan.PrestamoCartonFragmentArgs
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

        setupRecyclerView()
        loadCartonData()
    }

    private fun setupRecyclerView() {
        adapter = PrestamoCartonAdapter()
        binding.rvCarton.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCarton.adapter = adapter
    }

    private fun loadCartonData() {
        val prestamoId = arguments?.getString("prestamoId")
        if (prestamoId == null) {
            Toast.makeText(requireContext(), "ID de préstamo no proporcionado", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        lifecycleScope.launch {
            viewModel.obtenerAbonos(prestamoId).collect { abonos ->
                adapter.submitList(abonos)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
