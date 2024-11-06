package com.example.mediplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.R

data class HistorialExamenes(
    val id_usuario: String,
    val nombre_examen: String,
    val fecha: String,
    val hora: String,
    val especialidad: String
    //val entidad: String,
    //val nombre_doctor: String,
    //val descripcion: String
)

class HistorialExamenesAdapter(private val historialExamenes: List<HistorialExamenes>) : RecyclerView.Adapter<HistorialExamenesAdapter.HistorialExamenesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialExamenesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_historial_modulo_examenes, parent, false)
        return HistorialExamenesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialExamenesViewHolder, position: Int) {
        holder.bind(historialExamenes[position])
    }

    override fun getItemCount(): Int = historialExamenes.size

    class HistorialExamenesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreExamenTextView: TextView = itemView.findViewById(R.id.examenListar)
        private val fechaExamenTextView: TextView = itemView.findViewById(R.id.fechaexamenListar)
        private val horaExamenTextView: TextView = itemView.findViewById(R.id.horaexamenListar)
        private val especialidadTextView: TextView = itemView.findViewById(R.id.especialidadexamenListar)


        fun bind(examenes: HistorialExamenes) {
            nombreExamenTextView.text = examenes.nombre_examen
            fechaExamenTextView.text = examenes.fecha
            horaExamenTextView.text = examenes.hora
            especialidadTextView.text = examenes.especialidad
        }
    }
}