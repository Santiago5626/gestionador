package com.gestionador.ui.loan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.data.models.Abono
import com.gestionador.databinding.ItemCartonBinding
import java.text.SimpleDateFormat
import java.util.*

class PrestamoCartonAdapter : ListAdapter<Abono, PrestamoCartonAdapter.AbonoViewHolder>(DiffCallback) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbonoViewHolder {
        val binding = ItemCartonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbonoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbonoViewHolder, position: Int) {
        val abono = getItem(position)
        holder.bind(abono, position + 1)
    }

    inner class AbonoViewHolder(private val binding: ItemCartonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(abono: Abono, numeroCuota: Int) {
            binding.apply {
                tvNumeroCuota.text = numeroCuota.toString()
                // Remove tvFechaInicial as it does not exist in layout
                tvFechaAbono.text = dateFormat.format(Date(abono.fechaAbono))
                tvMontoAbonado.text = "$${String.format("%.2f", abono.montoAbonado)}"
                tvSaldoRestante.text = "$${String.format("%.2f", abono.saldoRestante)}"
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Abono>() {
        override fun areItemsTheSame(oldItem: Abono, newItem: Abono): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Abono, newItem: Abono): Boolean {
            return oldItem == newItem
        }
    }
}
