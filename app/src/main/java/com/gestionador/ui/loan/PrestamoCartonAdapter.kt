package com.gestionador.ui.loan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gestionador.data.models.Abono
import com.gestionador.databinding.ItemCartonBinding
import java.text.SimpleDateFormat
import java.util.*

class PrestamoCartonAdapter : ListAdapter<Abono, PrestamoCartonAdapter.AbonoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbonoViewHolder {
        val binding = ItemCartonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbonoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbonoViewHolder, position: Int) {
        val abono = getItem(position)
        holder.bind(abono)
    }

    inner class AbonoViewHolder(private val binding: ItemCartonBinding) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun bind(abono: Abono) {
            binding.apply {
                tvFechaAbono.text = dateFormat.format(Date(abono.fechaAbono))
                tvMontoAbonado.text = "$${String.format("%,.2f", abono.montoAbonado)}"
                tvSaldoRestante.text = "$${String.format("%,.2f", abono.saldoRestante)}"
                // Show valorDevolver if available, else hide or omit
                if (abono.valorDevolver != null) {
                    tvValorDevolver.text = "$${String.format("%,.2f", abono.valorDevolver)}"
                    tvValorDevolver.visibility = View.VISIBLE
                } else {
                    tvValorDevolver.visibility = View.GONE
            }
        }
    }
    }
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
