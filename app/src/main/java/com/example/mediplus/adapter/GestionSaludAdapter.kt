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
import com.example.mediplus.ConfirmacionEliminacionRecordatorioGestionSalud
import com.example.mediplus.EditarModuloGestionSalud
import com.example.mediplus.R
import com.example.mediplus.VerInfoModuloGestionSalud
import com.google.firebase.firestore.FirebaseFirestore

data class GestionSalud(
    val id_usuario: String,
    val enfermedad: String,
    val fecha: String,
    val hora: String,
    val categoria: String,
    val descripcion: String
)

class GestionSaludAdapter(private val gestionSalud: List<GestionSalud>) : RecyclerView.Adapter<GestionSaludAdapter.GestionSaludViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestionSaludViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_gestion_salud, parent, false)
        return GestionSaludViewHolder(view)
    }

    override fun onBindViewHolder(holder: GestionSaludViewHolder, position: Int) {
        holder.bind(gestionSalud[position])

        // Manejo del clic en el botón de tres puntos
        holder.buttonMenuPuntos.setOnClickListener { view ->
            showPopup(view, holder.itemView.context, gestionSalud[position], holder.buttonMenuPuntos)
        }
    }

    override fun getItemCount(): Int = gestionSalud.size

    class GestionSaludViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val enfermedadTextView: TextView = itemView.findViewById(R.id.txtActividadListarGestionSalud)
        private val fechaTextView: TextView = itemView.findViewById(R.id.txtFechaListarGestionSalud)
        private val categoriaTextView: TextView = itemView.findViewById(R.id.txtCategoriaListarGestionSalud)
        val buttonMenuPuntos: ImageView = itemView.findViewById(R.id.button_menu_puntos_gestion_salud)

        fun bind(vidaSaludable: GestionSalud) {
            enfermedadTextView.text = vidaSaludable.enfermedad
            fechaTextView.text = vidaSaludable.fecha
            categoriaTextView.text = vidaSaludable.categoria
        }
    }

    // Método para mostrar el popup
    private fun showPopup(view: View, context: Context, gestionSalud: GestionSalud, buttonMenu: ImageView) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_layout, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Ajusta el ancho del popup manualmente si lo necesitas
        popupWindow.width = 250 // Puedes ajustar el ancho en píxeles o dp

        // Maneja los clics en las opciones
        popupView.findViewById<TextView>(R.id.edit).setOnClickListener {
            //Toast.makeText(context, "Editar seleccionado: ${medicamento.id}", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, EditarModuloGestionSalud::class.java).apply {
                putExtra("id_usuario", gestionSalud.id_usuario)
                putExtra("enfermedad", gestionSalud.enfermedad)
                putExtra("fecha", gestionSalud.fecha)
                putExtra("hora", gestionSalud.hora)
                putExtra("categoria", gestionSalud.categoria)
                putExtra("descripcion", gestionSalud.descripcion)
            }
            context.startActivity(intent)
            popupWindow.dismiss()

        }
        popupView.findViewById<TextView>(R.id.delete).setOnClickListener {
            //Toast.makeText(context, "Eliminar seleccionado: ${medicamento.id_usuario}", Toast.LENGTH_SHORT).show()
            showDeleteConfirmationDialog(context) {
                // Eliminar el medicamento de Firestore
                eliminarGestionSalud(context, gestionSalud.id_usuario)
            }
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.info).setOnClickListener {
            //Toast.makeText(context, "Ver info seleccionado: ${medicamento.id_usuario}", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, VerInfoModuloGestionSalud::class.java).apply {
                putExtra("id_usuario", gestionSalud.id_usuario)
                putExtra("enfermedad", gestionSalud.enfermedad)
                putExtra("fecha", gestionSalud.fecha)
                putExtra("hora", gestionSalud.hora)
                putExtra("categoria", gestionSalud.categoria)
                putExtra("descripcion", gestionSalud.descripcion)
            }
            context.startActivity(intent)
            popupWindow.dismiss()
        }

        // Posicionar el popup alineado a la derecha del botón
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Muestra el popup a la derecha del botón
            val offsetX = buttonMenu.width - popupWindow.width
            popupWindow.showAsDropDown(buttonMenu, offsetX, 0)
        } else {
            popupWindow.showAsDropDown(buttonMenu)
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

    // Método para eliminar una actividad de Firestore y de la lista
    private fun eliminarGestionSalud(context: Context, id_usuario: String) {
        val db = FirebaseFirestore.getInstance()

        // Elimina el documento en Firestore
        db.collection("gestion_salud").document(id_usuario).delete()
            .addOnSuccessListener {
                // El documento se eliminó correctamente
                val nuevoListaGestionSalud = gestionSalud.filter { it.id_usuario != id_usuario }
                (gestionSalud as MutableList).clear()
                (gestionSalud as MutableList).addAll(nuevoListaGestionSalud)
                notifyDataSetChanged()
                //Toast.makeText(context, "Actividad eliminada de Firestore", Toast.LENGTH_SHORT).show()
                val confirm = Intent(context, ConfirmacionEliminacionRecordatorioGestionSalud::class.java)
                context.startActivity(confirm)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar el recordatorio: $e", Toast.LENGTH_SHORT).show()
            }
    }
}