package com.example.mediplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.R
import com.example.mediplus.adapter.HistorialMedicamentoAdapter.HistorialMedicamentoViewHolder

data class VidaSaludable(
    val id_usuario: String,
    val actividad: String,
    val categoria: String,
    val descripcion: String,
    val fecha: String,
    val hora: String
)

class HistorialVidaSaludableAdapter(private val vidaSaludable: List<VidaSaludable>) : RecyclerView.Adapter<HistorialVidaSaludableAdapter.HistorialVidaSaludableViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialVidaSaludableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_historial_vida_saludable, parent, false)
        return HistorialVidaSaludableViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialVidaSaludableViewHolder, position: Int) {
        holder.bind(vidaSaludable[position])
    }

    override fun getItemCount(): Int = vidaSaludable.size

    class HistorialVidaSaludableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val actividadTextView: TextView = itemView.findViewById(R.id.actividad_vida_saludable)
        private val categoriaTextView: TextView = itemView.findViewById(R.id.categoria_vida_saludable)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.descripcion_vida_saludable)
        private val fechaTextView: TextView = itemView.findViewById(R.id.fecha_vida_saludable)
        private val horaTextView: TextView = itemView.findViewById(R.id.hora_vida_saludable)

        fun bind(vidaSaludable: VidaSaludable) {
            actividadTextView.text = vidaSaludable.actividad
            categoriaTextView.text = vidaSaludable.categoria
            descripcionTextView.text = vidaSaludable.descripcion
            fechaTextView.text = vidaSaludable.fecha
            horaTextView.text = vidaSaludable.hora
        }
    }
}