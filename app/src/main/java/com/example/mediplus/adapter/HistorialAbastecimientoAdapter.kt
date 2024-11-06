package com.example.mediplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.R

data class HistorialAbastecimiento(
    val id_usuario: String,
    val medicamento: String,
    val fecha_abastecimiento: String,
    val cantidad: String
)

class HistorialAbastecimientoAdapter(private val historialAbastecimiento: List<HistorialAbastecimiento>) : RecyclerView.Adapter<HistorialAbastecimientoAdapter.HistorialAbastecimientoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialAbastecimientoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_historial_modulo_abastecimiento, parent, false)
        return HistorialAbastecimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialAbastecimientoViewHolder, position: Int) {
        holder.bind(historialAbastecimiento[position])
    }

    override fun getItemCount(): Int = historialAbastecimiento.size

    class HistorialAbastecimientoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicamentoAbastecimientoTextView: TextView = itemView.findViewById(R.id.medicamento_abastecimiento)
        private val fechaAbastecimientoTextView: TextView = itemView.findViewById(R.id.fecha_abastecimiento)
        private val cantidadAbastecimientoTextView: TextView = itemView.findViewById(R.id.cantidad_abastecimiento)

        fun bind(historial: HistorialAbastecimiento) {
            medicamentoAbastecimientoTextView.text = historial.medicamento
            fechaAbastecimientoTextView.text = historial.fecha_abastecimiento
            cantidadAbastecimientoTextView.text = historial.cantidad
        }
    }
}
