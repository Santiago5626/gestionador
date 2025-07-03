package com.gestionador.ui.loan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.data.models.Prestamo
import com.gestionador.data.models.TipoPrestamo
import com.gestionador.databinding.ItemPrestamoBinding

class PrestamosAdapter(
    private val onItemClick: (Prestamo) -> Unit,
    private val onEditClick: (Prestamo) -> Unit,
    private val onDeleteClick: (Prestamo) -> Unit,
    private val onAbonoClick: (Prestamo) -> Unit
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
            
            // Los botones btnEdit, btnDelete y btnAbono fueron removidos del layout, por lo que se comentan estos listeners
            /*
            binding.btnEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(getItem(position))
                }
            }
            
            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
            
            binding.btnAbono.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAbonoClick(getItem(position))
                }
            }
            */
        }

        fun bind(prestamo: Prestamo) {
                binding.apply {
                    chipTipo.text = prestamo.getTipoString()
                    tvCliente.text = prestamo.clienteNombre
                    tvMonto.text = "$${String.format("%.2f", prestamo.montoTotal)}"
                    
                    // Para préstamos mensuales, mostrar el interés en lugar de la cuota
                    if (prestamo.tipo == TipoPrestamo.MENSUAL) {
                        tvCuota.text = "Interés: ${String.format("%.1f", prestamo.porcentajeInteres)}%"
                    } else if (prestamo.tipo == TipoPrestamo.DIARIO || prestamo.tipo == TipoPrestamo.SEMANAL) {
                        // Para préstamos diarios y semanales, mostrar monto a prestar y monto a devolver
                        tvCuota.text = "Monto a prestar: $${String.format("%.2f", prestamo.montoTotal)}\nMonto a devolver: $${String.format("%.2f", prestamo.valorDevolver)}"
                    } else {
                        tvCuota.text = "Cuota: $${String.format("%.2f", prestamo.valorDevolver)}"
                    }
                    
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
