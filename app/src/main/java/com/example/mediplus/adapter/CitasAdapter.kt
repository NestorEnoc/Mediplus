package com.example.mediplus.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.ConfirmacionEliminacionRecordatorioCita
import com.example.mediplus.EditarModuloCitas
import com.example.mediplus.R
import com.example.mediplus.VerInfoModuloCitas
import com.google.firebase.firestore.FirebaseFirestore

data class Citas(
    val id_usuario: String,
    val especialidad: String,
    val fecha: String,
    val hora: String,
    val nombre_medico: String,
    val descripcion: String
)

class CitasAdapter(private val citas: List<Citas>) : RecyclerView.Adapter<CitasAdapter.CitasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_citas, parent, false)
        return CitasViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitasViewHolder, position: Int) {
        holder.bind(citas[position])

        // Manejo del clic en el botón de tres puntos
        holder.buttonMenuPuntos.setOnClickListener { view ->
            showPopup(view, holder.itemView.context, citas[position], holder.buttonMenuPuntos)
        }
    }

    override fun getItemCount(): Int = citas.size

    class CitasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.txtNombreCita)
        private val fechaTextView: TextView = itemView.findViewById(R.id.txtFechaCitas)
        private val dosisTextView: TextView = itemView.findViewById(R.id.txtEspecialidadListarCitas)
        val buttonMenuPuntos: ImageView = itemView.findViewById(R.id.button_menu_puntos)

        fun bind(cita: Citas) {
            nombreTextView.text = cita.especialidad
            fechaTextView.text = cita.fecha
            dosisTextView.text = cita.descripcion
        }
    }

    private fun showPopup(view: View, context: Context, cita: Citas, buttonMenu: ImageView) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_layout, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.width = 250 // Puedes ajustar el ancho en píxeles o dp

        // Maneja los clics en las opciones
        popupView.findViewById<TextView>(R.id.edit).setOnClickListener {
            val intent = Intent(context, EditarModuloCitas::class.java).apply {
                putExtra("id_usuario", cita.id_usuario)
                putExtra("especialidad", cita.especialidad)
                putExtra("fecha", cita.fecha)
                putExtra("hora", cita.hora)
                putExtra("nombre_medico", cita.nombre_medico)
                putExtra("descripcion", cita.descripcion)
            }
            context.startActivity(intent)
            popupWindow.dismiss()

        }

        popupView.findViewById<TextView>(R.id.delete).setOnClickListener {
            showDeleteConfirmationDialog(context) {
                // Eliminar el medicamento de Firestore
                eliminarCita(context, cita.id_usuario)
            }
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.info).setOnClickListener {
            //Toast.makeText(context, "Ver info seleccionado: ${medicamento.id_usuario}", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, VerInfoModuloCitas::class.java).apply {
                putExtra("id_usuario", cita.id_usuario)
                putExtra("especialidad", cita.especialidad)
                putExtra("fecha", cita.fecha)
                putExtra("hora", cita.hora)
                putExtra("nombre_medico", cita.nombre_medico)
                putExtra("descripcion", cita.descripcion)
            }
            context.startActivity(intent)
            popupWindow.dismiss()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Muestra el popup a la derecha del botón
            val offsetX = buttonMenu.width - popupWindow.width
            popupWindow.showAsDropDown(buttonMenu, offsetX, 0)
        } else {
            popupWindow.showAsDropDown(buttonMenu)
        }
}

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

    private fun eliminarCita(context: Context, id_usuario: String) {
        val db = FirebaseFirestore.getInstance()

        // Elimina el documento en Firestore
        db.collection("toma_citas").document(id_usuario).delete()
            .addOnSuccessListener {
                // El documento se eliminó correctamente
                val nuevoListaMedicamentos = citas.filter { it.id_usuario != id_usuario }
                (citas as MutableList).clear()
                (citas as MutableList).addAll(nuevoListaMedicamentos)
                notifyDataSetChanged()
                val confirm = Intent(context, ConfirmacionEliminacionRecordatorioCita::class.java)
                context.startActivity(confirm)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar cita: $e", Toast.LENGTH_SHORT).show()
            }
    }
}