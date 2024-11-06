package com.example.mediplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.R

data class HistorialCitas(
    val id_usuario: String,
    val especialidad: String,
    val fecha: String,
    val hora: String,
    val nombre_medico: String
)

class HistorialCitasAdapter(private val historialCitas: List<HistorialCitas>) : RecyclerView.Adapter<HistorialCitasAdapter.HistorialCitasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialCitasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_historial_modulo_citas, parent, false)
        return HistorialCitasViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialCitasViewHolder, position: Int) {
        holder.bind(historialCitas[position])
    }

    override fun getItemCount(): Int = historialCitas.size

    class HistorialCitasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val especialidadCitaTextView: TextView = itemView.findViewById(R.id.especialidadCitaListar)
        private val fechaCitaTextView: TextView = itemView.findViewById(R.id.fechaCitaListar)
        private val horaCitaTextView: TextView = itemView.findViewById(R.id.horaCitaListar)
        private val especialidadTextView: TextView = itemView.findViewById(R.id.nombreDoctorCitaListar)

        fun bind(citas: HistorialCitas) {
            especialidadCitaTextView.text = citas.especialidad
            fechaCitaTextView.text = citas.fecha
            horaCitaTextView.text = citas.hora
            especialidadTextView.text = citas.nombre_medico
        }
    }
}
