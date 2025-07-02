package com.gestionador.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gestionador.databinding.FragmentClienteDetailBinding

class ClienteDetailFragment : Fragment() {

    private var _binding: FragmentClienteDetailBinding? = null
    private val binding get() = _binding!!
    
    private var clienteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clienteId = arguments?.getString("clienteId")
    }

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
        
        // TODO: Load client details using clienteId
        clienteId?.let { id ->
            binding.textViewClienteId.text = "Cliente ID: $id"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
