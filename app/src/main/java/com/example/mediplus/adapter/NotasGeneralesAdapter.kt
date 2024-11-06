package com.example.mediplus.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.ConfirmacionEliminacionRecordatorioNotaGeneral
import com.example.mediplus.EditarModuloNotasGenerales
import com.example.mediplus.R
import com.example.mediplus.VerInfoModuloNotasGenerales
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

data class NotasGenerales(
    val id_usuario: String,
    val titulo_nota: String,
    val cuerpo_nota: String,
    val fecha: String,
    val hora: String,
)

class NotasGeneralesAdapter(private val notas_generales: List<NotasGenerales>) : RecyclerView.Adapter<NotasGeneralesAdapter.NotasGeneralesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotasGeneralesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_notas_generales, parent, false)
        return NotasGeneralesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotasGeneralesViewHolder, position: Int) {
        holder.bind(notas_generales[position])

        val context = holder.itemView.context

        // Acción del botón eliminar
        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(context) {
                // Llamada a la función para eliminar la nota de Firestore
                eliminarNotaGeneral(context, notas_generales[position].id_usuario)
            }
        }

        // Acción del botón editar
        holder.editButton.setOnClickListener {
            val intent = Intent(context, EditarModuloNotasGenerales::class.java).apply {
                putExtra("id_usuario", notas_generales[position].id_usuario)
                putExtra("titulo", notas_generales[position].titulo_nota)
                putExtra("cuerpo_nota", notas_generales[position].cuerpo_nota)
            }
            context.startActivity(intent)
        }

        // Acción del botón ver info
        holder.InfoButton.setOnClickListener {
            val intent = Intent(context, VerInfoModuloNotasGenerales::class.java).apply {
                putExtra("id_usuario", notas_generales[position].id_usuario)
                putExtra("titulo", notas_generales[position].titulo_nota)
                putExtra("cuerpo_nota", notas_generales[position].cuerpo_nota)
                putExtra("hora", notas_generales[position].hora)
                putExtra("fecha", notas_generales[position].fecha)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = notas_generales.size

    class NotasGeneralesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloTextView: TextView = itemView.findViewById(R.id.txtTituloNotaGeneral)
        private val horaTextView: TextView = itemView.findViewById(R.id.txtHoraNotaGeneral)
        private val fechaTextView: TextView = itemView.findViewById(R.id.txtFechaNotaGeneral)
        val deleteButton: MaterialCardView = itemView.findViewById(R.id.btn_eliminar_general)
        val editButton: MaterialCardView = itemView.findViewById(R.id.btn_editar_general)
        val InfoButton: MaterialCardView = itemView.findViewById(R.id.btn_info_general)

        fun bind(notas: NotasGenerales) {
            tituloTextView.text = notas.titulo_nota
            fechaTextView.text = notas.fecha
            horaTextView.text = notas.hora
        }
    }

    // Ventana Modal de eliminar
    private fun showDeleteConfirmationDialog(context: Context, onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirmar_eliminar, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_delete).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }

    // Método para eliminar una nota de Firestore y de la lista
    private fun eliminarNotaGeneral(context: Context, id_usuario: String) {
        val db = FirebaseFirestore.getInstance()

        // Elimina el documento en Firestore
        db.collection("notas_generales").document(id_usuario).delete()
            .addOnSuccessListener {
                // El documento se eliminó correctamente
                val nuevoListaNotasGenerales = notas_generales.filter { it.id_usuario != id_usuario }
                (notas_generales as MutableList).clear()
                (notas_generales as MutableList).addAll(nuevoListaNotasGenerales)
                notifyDataSetChanged()
                context.startActivity(Intent(context, ConfirmacionEliminacionRecordatorioNotaGeneral::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar la nota: $e", Toast.LENGTH_SHORT).show()
            }
    }
}
