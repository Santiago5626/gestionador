package com.gestionador.ui.asset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.R
import com.gestionador.data.models.Activo
import com.gestionador.data.models.CategoriaActivo
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
        holder.bind(getItem(position))
    }

    inner class ActivoViewHolder(
        private val binding: ItemActivoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activo: Activo) {
            binding.apply {
                textViewFecha.text = dateFormat.format(Date(activo.fecha))
                textViewMonto.text = currencyFormat.format(activo.montoIngresado)
                textViewDescripcion.text = activo.descripcion
                
                // Set categoria chip and color
                val (chipText, chipColor) = when (activo.categoria) {
                    CategoriaActivo.INGRESO -> Pair("INGRESO", R.color.colorSuccess)
                    CategoriaActivo.GASTO -> Pair("GASTO", R.color.colorError)
                    CategoriaActivo.INVERSION -> Pair("INVERSIÓN", R.color.colorInfo)
                }
                
                chipCategoria.text = chipText
                chipCategoria.setChipBackgroundColorResource(chipColor)
                
                // Set monto color based on categoria
                textViewMonto.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        when (activo.categoria) {
                            CategoriaActivo.INGRESO -> R.color.colorSuccess
                            CategoriaActivo.GASTO -> R.color.colorError
                            CategoriaActivo.INVERSION -> R.color.colorInfo
                        }
                    )
                )
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
