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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gestionador.R
import com.gestionador.databinding.FragmentClienteDetailBinding
import com.gestionador.ui.loan.PrestamosAdapter
import com.gestionador.ui.loan.PrestamosViewModel
import kotlinx.coroutines.launch

class ClienteDetailFragment : Fragment() {

    private var _binding: FragmentClienteDetailBinding? = null
    private val binding get() = _binding!!
    
    private val clientesViewModel: ClientesViewModel by viewModels()
    private val prestamosViewModel: PrestamosViewModel by viewModels()
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
        
        // Cargar datos del cliente y sus préstamos
        clientesViewModel.loadClientes()
        prestamosViewModel.loadPrestamos(args.clienteId)
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            // Navegar de vuelta a la lista de clientes
            findNavController().navigate(R.id.action_clienteDetailFragment_to_clientesFragment)
        }
        
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // Navegar a editar cliente
                    val bundle = bundleOf(
                        "clienteId" to args.clienteId,
                        "isEdit" to true
                    )
                    findNavController().navigate(R.id.action_clienteDetailFragment_to_addClienteFragment, bundle)
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
        prestamosAdapter = PrestamosAdapter(
            onItemClick = { prestamo ->
                // Navegar al detalle del préstamo
                val bundle = bundleOf("prestamoId" to prestamo.id)
                findNavController().navigate(R.id.action_clienteDetailFragment_to_prestamoDetailFragment, bundle)
            },
            onEditClick = { prestamo ->
                // Navegar a editar préstamo
                val bundle = bundleOf(
                    "prestamoId" to prestamo.id,
                    "isEdit" to true
                )
                findNavController().navigate(R.id.action_clienteDetailFragment_to_addPrestamoFragment, bundle)
            },
            onDeleteClick = { prestamo ->
                showDeletePrestamoConfirmationDialog(prestamo.id)
            }
        )
        
        binding.recyclerViewPrestamos.apply {
            adapter = prestamosAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupObservers() {
        // Observar datos del cliente
        viewLifecycleOwner.lifecycleScope.launch {
            clientesViewModel.clientes.collect { clientes ->
                val cliente = clientes.find { it.id == args.clienteId }
                cliente?.let {
                    binding.textViewNombre.text = it.getNombreCompleto()
                    binding.textViewCedula.text = "Cédula: ${it.cedula}"
                    binding.textViewTelefono.text = "Tel: ${it.telefono}"
                    binding.textViewDireccion.text = it.direccion
                }
            }
        }
        
        // Observar préstamos del cliente
        viewLifecycleOwner.lifecycleScope.launch {
            prestamosViewModel.prestamos.collect { prestamos ->
                prestamosAdapter.submitList(prestamos)
                binding.textViewNoPrestamos.visibility = if (prestamos.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        // Observar errores
        viewLifecycleOwner.lifecycleScope.launch {
            prestamosViewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Cliente")
            .setMessage("¿Está seguro que desea eliminar este cliente?\n\nEsta acción también eliminará todos sus préstamos.")
            .setPositiveButton("Eliminar") { _, _ ->
                clientesViewModel.deleteCliente(args.clienteId)
                // Navegar de vuelta a la lista de clientes
                findNavController().navigate(R.id.action_clienteDetailFragment_to_clientesFragment)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun showDeletePrestamoConfirmationDialog(prestamoId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Préstamo")
            .setMessage("¿Está seguro que desea eliminar este préstamo?\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                prestamosViewModel.deletePrestamo(prestamoId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
