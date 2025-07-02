package com.gestionador.ui.loan

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.R
import com.gestionador.data.models.Prestamo
import com.gestionador.data.models.TipoPrestamo
import com.gestionador.data.models.EstadoPrestamo
import com.gestionador.databinding.ItemPrestamoBinding
import java.text.NumberFormat
import java.util.*

class PrestamosAdapter(
    private val onPrestamoClick: (Prestamo) -> Unit
) : ListAdapter<Prestamo, PrestamosAdapter.PrestamoViewHolder>(PrestamoDiffCallback()) {

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
                tvClienteNombre.text = prestamo.clienteNombre
                tvMontoTotal.text = currencyFormat.format(prestamo.montoTotal)
                tvSaldoRestante.text = "Restante: ${currencyFormat.format(prestamo.saldoRestante)}"
                tvCuota.text = "Cuota: ${currencyFormat.format(prestamo.valorCuotaPactada)}"
                tvEstado.text = prestamo.getEstadoString()

                // Configurar chip de tipo de préstamo
                chipTipo.text = prestamo.getTipoString()
                val chipColor = when (prestamo.tipo) {
                    TipoPrestamo.DIARIO -> R.color.prestamoDiario
                    TipoPrestamo.SEMANAL -> R.color.prestamoSemanal
                    TipoPrestamo.MENSUAL -> R.color.prestamoMensual
                }
                chipTipo.setChipBackgroundColorResource(chipColor)

                // Configurar barra de progreso
                val progreso = ((prestamo.montoTotal - prestamo.saldoRestante) / prestamo.montoTotal * 100).toInt()
                progressBar.progress = progreso
                progressBar.setIndicatorColor(ContextCompat.getColor(root.context, chipColor))
                progressBar.trackColor = ContextCompat.getColor(root.context, R.color.surfaceVariant)

                // Configurar color del estado
                val estadoColor = when (prestamo.estado) {
                    EstadoPrestamo.ACTIVO -> R.color.colorSuccess
                    EstadoPrestamo.PAGADO -> R.color.colorInfo
                    EstadoPrestamo.VENCIDO -> R.color.colorError
                }
                tvEstado.setTextColor(ContextCompat.getColor(root.context, estadoColor))

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
