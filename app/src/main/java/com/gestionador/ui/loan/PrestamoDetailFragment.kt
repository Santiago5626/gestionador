package com.gestionador.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gestionador.databinding.FragmentPrestamoDetailBinding

class PrestamoDetailFragment : Fragment() {

    private var _binding: FragmentPrestamoDetailBinding? = null
    private val binding get() = _binding!!
    
    private var prestamoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prestamoId = arguments?.getString("prestamoId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrestamoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // TODO: Load prestamo details using prestamoId
        prestamoId?.let { id ->
            binding.textViewPrestamoId.text = "Préstamo ID: $id"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
