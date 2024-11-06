package com.example.mediplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.R

data class HistorialMedicamento(
    val id_usuario: String,
    val nombre_medicamento: String,
    val fecha: String,
    val hora: String,
    val num_dosis: String
)

class HistorialMedicamentoAdapter(private val historialMedicamento: List<HistorialMedicamento>) : RecyclerView.Adapter<HistorialMedicamentoAdapter.HistorialMedicamentoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialMedicamentoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_historial_modulo_medicamentos, parent, false)
        return HistorialMedicamentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialMedicamentoViewHolder, position: Int) {
        holder.bind(historialMedicamento[position])

    }

    override fun getItemCount(): Int = historialMedicamento.size

    class HistorialMedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreMedicamentoTextView: TextView = itemView.findViewById(R.id.nombre_medicamento)
        private val fechaMedicamentoTextView: TextView = itemView.findViewById(R.id.fecha_medicamento)
        private val horaMedicamentoTextView: TextView = itemView.findViewById(R.id.hora_medicamento)
        private val dosisMedicamentoTextView: TextView = itemView.findViewById(R.id.dosis_medicamento)

        fun bind(vidaSaludable: HistorialMedicamento) {
            nombreMedicamentoTextView.text = vidaSaludable.nombre_medicamento
            fechaMedicamentoTextView.text = vidaSaludable.fecha
            horaMedicamentoTextView.text = vidaSaludable.hora
            dosisMedicamentoTextView.text = vidaSaludable.num_dosis
        }
    }

}