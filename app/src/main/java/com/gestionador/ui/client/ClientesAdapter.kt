package com.gestionador.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.data.models.Cliente
import com.gestionador.databinding.ItemClienteBinding

class ClientesAdapter(
    private val onClienteClick: (Cliente) -> Unit
) : ListAdapter<Cliente, ClientesAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ItemClienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClienteViewHolder(
        private val binding: ItemClienteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cliente: Cliente) {
            binding.apply {
                tvNombre.text = cliente.getNombreCompleto()
                tvCedula.text = "CC: ${cliente.cedula}"
                tvTelefono.text = "📞 ${cliente.telefono}"
                tvDireccion.text = "📍 ${cliente.direccion}"
                
                // Generar iniciales para el avatar
                val iniciales = "${cliente.nombre.firstOrNull()?.uppercase() ?: ""}${cliente.apellido.firstOrNull()?.uppercase() ?: ""}"
                tvAvatar.text = iniciales

                root.setOnClickListener {
                    onClienteClick(cliente)
                }
            }
        }
    }

    private class ClienteDiffCallback : DiffUtil.ItemCallback<Cliente>() {
        override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem == newItem
        }
    }
}
