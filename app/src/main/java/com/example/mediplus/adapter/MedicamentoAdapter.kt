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
import com.example.mediplus.EditarModuloMedicamentos
import com.example.mediplus.R
import com.example.mediplus.VerInfoModuloMedicamentos
import android.app.AlertDialog
import com.example.mediplus.ConfirmacionEliminacionRecordatorioMedicamento
import com.google.firebase.firestore.FirebaseFirestore

// Clase Medicamento dentro del adaptador
/*data class Medicamento(
    val nombre_medicamento: String, val fecha_desde: String, val num_dosis: String, val id_usuario: String,
    val descripcion: String, val fecha_hasta: String, val hora: String, val hora_mas_veces: String,
    val intervalo_horas: String, val presentacion: String, val unidad: String)*/

data class Medicamento(
    val nombre_medicamento: String,
    val fecha_desde: String,
    val num_dosis: String,
    val id_usuario: String,
    val descripcion: String,
    val fecha_hasta: String,
    val hora: String,
    val hora_mas_veces: String,
    val intervalo_horas: String,
    val presentacion: String,
    val unidad: String
)

// MedicamentoAdapter.kt - Adaptador del RecyclerView
class MedicamentoAdapter(private val medicamentos: List<Medicamento>) : RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_toma_medicamentos, parent, false)
        return MedicamentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        holder.bind(medicamentos[position])

        // Manejo del clic en el botón de tres puntos
        holder.buttonMenuPuntos.setOnClickListener { view ->
            showPopup(view, holder.itemView.context, medicamentos[position], holder.buttonMenuPuntos)
        }
    }

    override fun getItemCount(): Int = medicamentos.size

    class MedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.txtNombreMedicamento)
        private val fechaTextView: TextView = itemView.findViewById(R.id.txtFechaIni)
        private val dosisTextView: TextView = itemView.findViewById(R.id.txtDosisData)
        val buttonMenuPuntos: ImageView = itemView.findViewById(R.id.button_menu_puntos)

        fun bind(medicamento: Medicamento) {
            nombreTextView.text = medicamento.nombre_medicamento
            fechaTextView.text = medicamento.fecha_desde
            dosisTextView.text = medicamento.num_dosis
        }
    }

    // Método para mostrar el popup
    private fun showPopup(view: View, context: Context, medicamento: Medicamento, buttonMenu: ImageView) {
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
            val intent = Intent(context, EditarModuloMedicamentos::class.java).apply {
                putExtra("id_usuario", medicamento.id_usuario)
                putExtra("nombre_medicamento", medicamento.nombre_medicamento)
                putExtra("fecha_desde", medicamento.fecha_desde)
                putExtra("num_dosis", medicamento.num_dosis)
                putExtra("descripcion", medicamento.descripcion)
                putExtra("fecha_hasta", medicamento.fecha_hasta)
                putExtra("hora", medicamento.hora)
                putExtra("hora_mas_veces", medicamento.hora_mas_veces)
                putExtra("intervalo_horas", medicamento.intervalo_horas)
                putExtra("presentacion", medicamento.presentacion)
                putExtra("unidad", medicamento.unidad)
            }
            context.startActivity(intent)
            popupWindow.dismiss()

        }
        popupView.findViewById<TextView>(R.id.delete).setOnClickListener {
            //Toast.makeText(context, "Eliminar seleccionado: ${medicamento.id_usuario}", Toast.LENGTH_SHORT).show()
            showDeleteConfirmationDialog(context) {
                // Eliminar el medicamento de Firestore
                eliminarMedicamento(context, medicamento.id_usuario)
            }
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.info).setOnClickListener {
            //Toast.makeText(context, "Ver info seleccionado: ${medicamento.id_usuario}", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, VerInfoModuloMedicamentos::class.java).apply {
                putExtra("id_usuario", medicamento.id_usuario)
                putExtra("nombre_medicamento", medicamento.nombre_medicamento)
                putExtra("presentacion", medicamento.presentacion)
                putExtra("fecha_desde", medicamento.fecha_desde)
                putExtra("fecha_hasta", medicamento.fecha_hasta)
                putExtra("num_dosis", medicamento.num_dosis)
                putExtra("unidad", medicamento.unidad)
                putExtra("descripcion", medicamento.descripcion)
                putExtra("hora", medicamento.hora)
                putExtra("hora_mas_veces", medicamento.hora_mas_veces)
                putExtra("intervalo_horas", medicamento.intervalo_horas)
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

    // Método para eliminar un medicamento de Firestore y de la lista
    private fun eliminarMedicamento(context: Context, id_usuario: String) {
        val db = FirebaseFirestore.getInstance()

        // Elimina el documento en Firestore
        db.collection("toma_medicamentos").document(id_usuario).delete()
            .addOnSuccessListener {
                // El documento se eliminó correctamente
                val nuevoListaMedicamentos = medicamentos.filter { it.id_usuario != id_usuario }
                (medicamentos as MutableList).clear()
                (medicamentos as MutableList).addAll(nuevoListaMedicamentos)
                notifyDataSetChanged()
                //Toast.makeText(context, "Medicamento eliminado de Firestore", Toast.LENGTH_SHORT).show()
                val confirm = Intent(context, ConfirmacionEliminacionRecordatorioMedicamento::class.java)
                context.startActivity(confirm)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar medicamento: $e", Toast.LENGTH_SHORT).show()
            }
    }
}


