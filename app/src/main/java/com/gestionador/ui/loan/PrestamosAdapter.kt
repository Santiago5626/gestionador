package com.gestionador.ui.loan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.data.models.Prestamo
import com.gestionador.data.models.TipoPrestamo
import com.gestionador.data.models.EstadoPrestamo
import com.gestionador.databinding.ItemPrestamoBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PrestamosAdapter(
    private val onPrestamoClick: (Prestamo) -> Unit
) : ListAdapter<Prestamo, PrestamosAdapter.PrestamoViewHolder>(PrestamoDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val binding = ItemPrestamoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrestamoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PrestamoViewHolder(
        private val binding: ItemPrestamoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(prestamo: Prestamo) {
            binding.apply {
                textViewTipo.text = when (prestamo.tipo) {
                    TipoPrestamo.DIARIO -> "Diario"
                    TipoPrestamo.SEMANAL -> "Semanal"
                    TipoPrestamo.MENSUAL -> "Mensual"
                }
                
                textViewFecha.text = dateFormat.format(Date(prestamo.fechaInicial))
                textViewMonto.text = currencyFormat.format(prestamo.montoTotal)
                textViewSaldo.text = "Saldo: ${currencyFormat.format(prestamo.saldoRestante)}"
                textViewCuota.text = "Cuota: ${currencyFormat.format(prestamo.valorCuotaPactada)}"
                
                // Set estado color
                val estadoColor = when (prestamo.estado) {
                    EstadoPrestamo.ACTIVO -> android.R.color.holo_green_dark
                    EstadoPrestamo.PAGADO -> android.R.color.holo_blue_dark
                    EstadoPrestamo.VENCIDO -> android.R.color.holo_red_dark
                }
                textViewEstado.setTextColor(root.context.getColor(estadoColor))
                textViewEstado.text = prestamo.estado.name

                root.setOnClickListener {
                    onPrestamoClick(prestamo)
                }
            }
        }
    }

    private class PrestamoDiffCallback : DiffUtil.ItemCallback<Prestamo>() {
        override fun areItemsTheSame(oldItem: Prestamo, newItem: Prestamo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Prestamo, newItem: Prestamo): Boolean {
            return oldItem == newItem
        }
    }
}
