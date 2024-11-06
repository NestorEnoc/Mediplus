package com.example.mediplus.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.R
import com.example.mediplus.ReabastecerMedicamentos
import com.example.mediplus.VerInfoModuloExamenes

data class Examenes(
    val id_usuario: String,
    val nombre_examen: String,
    val fecha: String,
    val hora: String,
    val especialidad: String,
    val entidad: String,
    val nombre_doctor: String,
    val descripcion: String
)

class ExamenesAdapter(private val examenes: List<Examenes>) : RecyclerView.Adapter<ExamenesAdapter.ExamenesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamenesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_examenes, parent, false)
        return ExamenesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExamenesViewHolder, position: Int) {
        holder.bind(examenes[position])

        // Manejo del clic en el botón de tres puntos
        holder.buttonVerInfo.setOnClickListener { view ->
            val context = holder.itemView.context
            val examen = examenes[position]

            // Crear un Intent para redirigir a la nueva Activity
            val intent = Intent(context, VerInfoModuloExamenes::class.java).apply {
                putExtra("id_usuario", examen.id_usuario)
                putExtra("nombre_examen", examen.nombre_examen)
                putExtra("fecha", examen.fecha)
                putExtra("hora", examen.hora)
                putExtra("especialidad", examen.especialidad)
                putExtra("entidad", examen.entidad)
                putExtra("nombre_doctor", examen.nombre_doctor)
                putExtra("descripcion", examen.descripcion)
            }
            // Iniciar la nueva Activity
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = examenes.size

    class ExamenesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val especialidadTextView: TextView = itemView.findViewById(R.id.txtEspecialidadListar)
        private val fechaTextView: TextView = itemView.findViewById(R.id.txtFechaExamenListar)
        val buttonVerInfo: ImageView = itemView.findViewById(R.id.ver_info_examen)

        fun bind(examenes: Examenes) {
            especialidadTextView.text = examenes.especialidad
            fechaTextView.text = examenes.fecha
        }
    }

}