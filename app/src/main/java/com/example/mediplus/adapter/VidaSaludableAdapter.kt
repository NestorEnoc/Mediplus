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
import com.example.mediplus.ConfirmacionEliminacionRecordatorioVidaSaludable
import com.example.mediplus.EditarModuloMedicamentos
import com.example.mediplus.EditarModuloVidaSaludable
import com.example.mediplus.R
import com.example.mediplus.VerInfoModuloMedicamentos
import com.example.mediplus.VerInfoModuloVidaSaludable
import com.google.firebase.firestore.FirebaseFirestore

data class Salud(
    val actividad: String,
    val categoria: String,
    val id_usuario: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
)

class VidaSaludableAdapter(private val vidaSaludable: List<Salud>) : RecyclerView.Adapter<VidaSaludableAdapter.VidaSaludableViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VidaSaludableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_vida_saludable, parent, false)
        return VidaSaludableViewHolder(view)
    }

    override fun onBindViewHolder(holder: VidaSaludableViewHolder, position: Int) {
        holder.bind(vidaSaludable[position])

        // Manejo del clic en el botón de tres puntos
        holder.buttonMenuPuntos.setOnClickListener { view ->
            showPopup(view, holder.itemView.context, vidaSaludable[position], holder.buttonMenuPuntos)
        }
    }

    override fun getItemCount(): Int = vidaSaludable.size

    class VidaSaludableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val actividadTextView: TextView = itemView.findViewById(R.id.txtActividadListarVidaSaludable)
        private val fechaTextView: TextView = itemView.findViewById(R.id.txtFechaListarVidaSaludable)
        private val categoriaTextView: TextView = itemView.findViewById(R.id.txtCategoriaListarVidaSaludable)
        val buttonMenuPuntos: ImageView = itemView.findViewById(R.id.button_menu_puntos_vida_saludable)

        fun bind(vidaSaludable: Salud) {
            actividadTextView.text = vidaSaludable.actividad
            fechaTextView.text = vidaSaludable.fecha
            categoriaTextView.text = vidaSaludable.categoria
        }
    }

    // Método para mostrar el popup
    private fun showPopup(view: View, context: Context, vidaSaludable: Salud, buttonMenu: ImageView) {
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
            val intent = Intent(context, EditarModuloVidaSaludable::class.java).apply {
                putExtra("id_usuario", vidaSaludable.id_usuario)
                putExtra("actividad", vidaSaludable.actividad)
                putExtra("categoria", vidaSaludable.categoria)
                putExtra("fecha", vidaSaludable.fecha)
                putExtra("descripcion", vidaSaludable.descripcion)
                putExtra("hora", vidaSaludable.hora)
            }
            context.startActivity(intent)
            popupWindow.dismiss()

        }
        popupView.findViewById<TextView>(R.id.delete).setOnClickListener {
            //Toast.makeText(context, "Eliminar seleccionado: ${medicamento.id_usuario}", Toast.LENGTH_SHORT).show()
            showDeleteConfirmationDialog(context) {
                // Eliminar el medicamento de Firestore
                eliminarAbastecimiento(context, vidaSaludable.id_usuario)
            }
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.info).setOnClickListener {
            //Toast.makeText(context, "Ver info seleccionado: ${medicamento.id_usuario}", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, VerInfoModuloVidaSaludable::class.java).apply {
                putExtra("id_usuario", vidaSaludable.id_usuario)
                putExtra("actividad", vidaSaludable.actividad)
                putExtra("categoria", vidaSaludable.categoria)
                putExtra("fecha", vidaSaludable.fecha)
                putExtra("descripcion", vidaSaludable.descripcion)
                putExtra("hora", vidaSaludable.hora)
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
    private fun eliminarAbastecimiento(context: Context, id_usuario: String) {
        val db = FirebaseFirestore.getInstance()

        // Elimina el documento en Firestore
        db.collection("vida_saludable").document(id_usuario).delete()
            .addOnSuccessListener {
                // El documento se eliminó correctamente
                val nuevoListaVidaSaludable = vidaSaludable.filter { it.id_usuario != id_usuario }
                (vidaSaludable as MutableList).clear()
                (vidaSaludable as MutableList).addAll(nuevoListaVidaSaludable)
                notifyDataSetChanged()
                //Toast.makeText(context, "Actividad eliminada de Firestore", Toast.LENGTH_SHORT).show()
                val confirm = Intent(context, ConfirmacionEliminacionRecordatorioVidaSaludable::class.java)
                context.startActivity(confirm)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar la actividad: $e", Toast.LENGTH_SHORT).show()
            }
    }
}