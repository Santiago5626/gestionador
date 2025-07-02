package com.gestionador.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gestionador.R
import com.gestionador.databinding.FragmentClienteDetailBinding
import com.gestionador.ui.loan.PrestamosAdapter
import kotlinx.coroutines.launch

class ClienteDetailFragment : Fragment() {

    private var _binding: FragmentClienteDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ClientesViewModel by viewModels()
    private val args: ClienteDetailFragmentArgs by navArgs()
    private lateinit var prestamosAdapter: PrestamosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClienteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        viewModel.loadClientes()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // TODO: Navigate to edit client
                    true
                }
                R.id.action_delete -> {
                    showDeleteConfirmationDialog()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupRecyclerView() {
        prestamosAdapter = PrestamosAdapter { prestamo ->
            // TODO: Navigate to prestamo detail
        }
        
        binding.recyclerViewPrestamos.apply {
            adapter = prestamosAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.clientes.collect { clientes ->
                val cliente = clientes.find { it.id == args.clienteId }
                cliente?.let {
                    binding.textViewClienteId.text = "ID: ${it.id}"
                    binding.textViewNombre.text = "${it.nombre} ${it.apellido}"
                    binding.textViewCedula.text = "Cédula: ${it.cedula}"
                    binding.textViewTelefono.text = "Tel: ${it.telefono}"
                    binding.textViewDireccion.text = it.direccion
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddPrestamo.setOnClickListener {
            // TODO: Navigate to add prestamo
        }
    }
    
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Cliente")
            .setMessage("¿Está seguro que desea eliminar este cliente?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteCliente(args.clienteId)
                findNavController().navigateUp()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
