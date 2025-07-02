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

class ActivosAdapter : ListAdapter<Activo, ActivosAdapter.ActivoViewHolder>(ActivoDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

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
                tvMonto.text = "+${currencyFormat.format(activo.monto)}"
                // Combinar procedencia y descripción en el campo descripción
                val descripcionCompleta = if (activo.procedencia.isNotEmpty()) {
                    "${activo.procedencia}: ${activo.descripcion}"
                } else {
                    activo.descripcion
                }
                tvDescripcion.text = descripcionCompleta
                
                // Ocultar la línea de tiempo en el último elemento
                if (isLast) {
                    timelineLine.visibility = android.view.View.INVISIBLE
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
