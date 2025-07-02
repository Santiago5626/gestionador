package com.gestionador.ui.asset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.data.models.Activo
import com.gestionador.databinding.ItemActivoBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ActivosAdapter(
    private val onEditClick: (Activo) -> Unit,
    private val onDeleteClick: (Activo) -> Unit
) : ListAdapter<Activo, ActivosAdapter.ActivoViewHolder>(ActivoDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val numberFormat = NumberFormat.getNumberInstance(Locale("es", "CO"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivoViewHolder {
        val binding = ItemActivoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActivoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivoViewHolder, position: Int) {
        holder.bind(getItem(position), position == itemCount - 1)
    }

    inner class ActivoViewHolder(
        private val binding: ItemActivoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activo: Activo, isLast: Boolean) {
            binding.apply {
                tvFecha.text = dateFormat.format(activo.fechaCreacion)
                tvMonto.text = "+$${numberFormat.format(activo.monto.toLong())}"
                tvDescripcion.text = activo.descripcion
                tvProcedencia.text = "Procedencia: ${activo.procedencia}"
                
                // Ocultar la línea de tiempo en el último elemento
                if (isLast) {
                    timelineLine.visibility = android.view.View.INVISIBLE
                }
                
                btnEdit.setOnClickListener {
                    onEditClick(activo)
                }
                
                btnDelete.setOnClickListener {
                    onDeleteClick(activo)
                }
            }
        }
    }

    private class ActivoDiffCallback : DiffUtil.ItemCallback<Activo>() {
        override fun areItemsTheSame(oldItem: Activo, newItem: Activo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Activo, newItem: Activo): Boolean {
            return oldItem == newItem
        }
    }
}
