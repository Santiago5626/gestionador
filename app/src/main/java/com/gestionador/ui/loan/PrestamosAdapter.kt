package com.gestionador.ui.loan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.data.models.Prestamo
import com.gestionador.databinding.ItemPrestamoBinding

class PrestamosAdapter(
    private val onItemClick: (Prestamo) -> Unit
) : ListAdapter<Prestamo, PrestamosAdapter.PrestamoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val binding = ItemPrestamoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrestamoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        val prestamo = getItem(position)
        holder.bind(prestamo)
    }

    inner class PrestamoViewHolder(
        private val binding: ItemPrestamoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(prestamo: Prestamo) {
            binding.apply {
                chipTipo.text = prestamo.getTipoString()
                tvCliente.text = prestamo.clienteNombre
                tvMonto.text = "$${String.format("%.2f", prestamo.montoTotal)}"
                tvCuota.text = "Cuota: $${String.format("%.2f", prestamo.valorCuotaPactada)}"
                
                // Calculate progress based on payments made
                val progress = calculateProgress(prestamo)
                progressBar.progress = progress
            }
        }
        
        private fun calculateProgress(prestamo: Prestamo): Int {
            // Calculate progress as percentage of amount paid
            val amountPaid = prestamo.montoTotal - prestamo.saldoRestante
            return ((amountPaid / prestamo.montoTotal) * 100).toInt()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Prestamo>() {
        override fun areItemsTheSame(oldItem: Prestamo, newItem: Prestamo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Prestamo, newItem: Prestamo): Boolean {
            return oldItem == newItem
        }
    }
}
