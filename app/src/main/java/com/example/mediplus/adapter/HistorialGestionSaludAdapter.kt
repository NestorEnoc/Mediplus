package com.example.mediplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.R

data class HistorialGestionSalud(
    val id_usuario: String,
    val enfermedad: String,
    val fecha: String,
    val hora: String,
    val categoria: String,
    val descripcion: String
)

class HistorialGestionSaludAdapter(private val historialGestionSalud: List<HistorialGestionSalud>) : RecyclerView.Adapter<HistorialGestionSaludAdapter.HistorialGestionSaludViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialGestionSaludViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_historial_modulo_gestion_salud, parent, false)
        return HistorialGestionSaludViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialGestionSaludViewHolder, position: Int) {
        holder.bind(historialGestionSalud[position])
    }

    override fun getItemCount(): Int = historialGestionSalud.size

    class HistorialGestionSaludViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val enfermedadoGestionSaludTextView: TextView = itemView.findViewById(R.id.enfermedadListarGestonSalud)
        private val fechaGestionSaludTextView: TextView = itemView.findViewById(R.id.fechaListarGestonSalud)
        private val horaGestionSaludTextView: TextView = itemView.findViewById(R.id.horaListarGestonSalud)
        private val categoriaGestionSaludTextView: TextView = itemView.findViewById(R.id.categoriaListarGestonSalud)
        private val descripcionGestionSaludTextView: TextView = itemView.findViewById(R.id.descripcionListarGestonSalud)

        fun bind(historial: HistorialGestionSalud) {
            enfermedadoGestionSaludTextView.text = historial.enfermedad
            fechaGestionSaludTextView.text = historial.fecha
            horaGestionSaludTextView.text = historial.hora
            categoriaGestionSaludTextView.text = historial.categoria
            descripcionGestionSaludTextView.text = historial.descripcion
        }
    }
}
