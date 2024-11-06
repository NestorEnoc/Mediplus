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
import com.example.mediplus.ConfirmacionEliminacionRecordatorioNotaMedicamento
import com.example.mediplus.EditarModuloMedicamentos
import com.example.mediplus.EditarModuloNotasMedicamentos
import com.example.mediplus.R
import com.example.mediplus.VerInfoModuloNotasMedicamentos
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

data class NotasMedicamento(
    val id_usuario: String,
    val titulo_nota: String,
    val medicamento: String,
    val cuerpo_nota: String,
    val fecha: String,
    val hora: String,
)

class NotasMedicamentoAdapter(private val notas_medicamentos: List<NotasMedicamento>) : RecyclerView.Adapter<NotasMedicamentoAdapter.NotasMedicamentoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotasMedicamentoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_notas_medicamentos, parent, false)
        return NotasMedicamentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotasMedicamentoViewHolder, position: Int) {
        holder.bind(notas_medicamentos[position])

        val context = holder.itemView.context

        // Accion del boton eliminar
        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(context) {
                // Llamada a la función para eliminar el medicamento de Firestore
                eliminarNotaMedicamento(context, notas_medicamentos[position].id_usuario)
            }
        }

        // Accion del boton editar
        holder.editButton.setOnClickListener {
            val intent = Intent(context, EditarModuloNotasMedicamentos::class.java).apply {
                putExtra("id_usuario", notas_medicamentos[position].id_usuario)
                putExtra("titulo", notas_medicamentos[position].titulo_nota)
                putExtra("medicamento", notas_medicamentos[position].medicamento)
                putExtra("cuerpo_nota", notas_medicamentos[position].cuerpo_nota)
            }
            context.startActivity(intent)

        }

        // Accion del boton ver info
        holder.InfoButton.setOnClickListener {
            val intent = Intent(context, VerInfoModuloNotasMedicamentos::class.java).apply {
                putExtra("id_usuario", notas_medicamentos[position].id_usuario)
                putExtra("titulo", notas_medicamentos[position].titulo_nota)
                putExtra("medicamento", notas_medicamentos[position].medicamento)
                putExtra("cuerpo_nota", notas_medicamentos[position].cuerpo_nota)
                putExtra("hora", notas_medicamentos[position].hora)
                putExtra("fecha", notas_medicamentos[position].fecha)
            }
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int = notas_medicamentos.size

    class NotasMedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloTextView: TextView = itemView.findViewById(R.id.txtTituloNota)
        private val horaTextView: TextView = itemView.findViewById(R.id.txtHoraNotaMedicamento)
        private val fechaTextView: TextView = itemView.findViewById(R.id.txtFechaNotaMedicamento)
        private val medicamentoTextView: TextView = itemView.findViewById(R.id.txtMedicamentoNotas)
        val deleteButton: MaterialCardView = itemView.findViewById(R.id.btn_eliminar)
        val editButton: MaterialCardView = itemView.findViewById(R.id.btn_editar)
        val InfoButton: MaterialCardView = itemView.findViewById(R.id.btn_info)

        fun bind(notas: NotasMedicamento) {
            tituloTextView.text = notas.titulo_nota
            fechaTextView.text = notas.fecha
            horaTextView.text = notas.hora
            medicamentoTextView.text = notas.medicamento
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

    // Método para eliminar un medicamento de Firestore y de la lista
    private fun eliminarNotaMedicamento(context: Context, id_usuario: String) {
        val db = FirebaseFirestore.getInstance()

        // Elimina el documento en Firestore
        db.collection("notas_medicamentos").document(id_usuario).delete()
            .addOnSuccessListener {
                // El documento se eliminó correctamente
                val nuevoListaNotasMedicamentos = notas_medicamentos.filter { it.id_usuario != id_usuario }
                (notas_medicamentos as MutableList).clear()
                (notas_medicamentos as MutableList).addAll(nuevoListaNotasMedicamentos)
                notifyDataSetChanged()
                context.startActivity(Intent(context, ConfirmacionEliminacionRecordatorioNotaMedicamento::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar la nota: $e", Toast.LENGTH_SHORT).show()
            }
    }
}
