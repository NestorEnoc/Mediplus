package com.example.mediplus

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mediplus.adapter.AbastecimientoMedicamento
import com.google.firebase.firestore.FirebaseFirestore

object abastecimiento_data {
    var id_usuario: String? = null
    var medicamento: String? = null
    var fecha_abastecimiento: String? = null
    var cantidad: String? = null
}

class VerInfoModuloAbastecimiento : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_info_modulo_abastecimiento)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        /* Codigo para traer las iniciales del usuario logueado y topbarReturn*/
        val usuarioLogeado = sharedPreferences.getString("usuarioLogeado", "")

        // Crear una nueva instancia del fragmento
        val fragment = BarTopReturn().apply {
            arguments = Bundle().apply {
                putString("usuarioLogeado", usuarioLogeado) // Pasa el usuario logueado como argumento
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        /**/

        // Agregar el fragmento de la bottom bar
        val fragmentBar = BarBottom()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_bottom_bar, fragmentBar)
            .commit()

        /**/

        // Crear un Map que asocie las claves de los extras con las variables globales
        val extrasMap = mapOf(
            "id_usuario" to { abastecimiento_data.id_usuario = intent.getStringExtra("id_usuario") },
            "medicamento" to { abastecimiento_data.medicamento = intent.getStringExtra("medicamento") },
            "fecha_abastecimiento" to { abastecimiento_data.fecha_abastecimiento = intent.getStringExtra("fecha_abastecimiento") },
            "cantidad" to { abastecimiento_data.cantidad = intent.getStringExtra("cantidad") }
        )

        // Asignar los datos del Intent a las variables globales
        extrasMap.forEach { it.value() }

        // Crear un Map que asocie las IDs de los TextViews con las variables globales
        val textViewMap = mapOf(
            R.id.txtMedicamentoListarAbast to abastecimiento_data.medicamento,
            R.id.txtPresentacionListarAbast to abastecimiento_data.fecha_abastecimiento,
            R.id.txtCantidadListarAbast to abastecimiento_data.cantidad
        )

        // Asignar los datos a los TextViews
        textViewMap.forEach { (viewId, value) ->
            findViewById<TextView>(viewId).text = value
        }


        // Accion del boton Reabastecer
        findViewById<LinearLayout>(R.id.ButtonReabastecerDesdeVer).setOnClickListener {
            val intent = Intent(this, ReabastecerMedicamentos::class.java).apply {
                putExtra("id_usuario", abastecimiento_data.id_usuario)
                putExtra("medicamento", abastecimiento_data.medicamento)
                putExtra("cantidad", abastecimiento_data.cantidad)
            }
            startActivity(intent)
        }

        // Accion del boton Eliminar
        findViewById<LinearLayout>(R.id.ButtonEliminarReabastecimientoDesdeVer).setOnClickListener {
            showDeleteConfirmationDialog {
                // Eliminar el medicamento de Firestore
                if (abastecimiento_data.id_usuario != null) {
                    eliminarAbastecimientos(abastecimiento_data.id_usuario!!)
                }
            }

        }

        // Accion del boton Archivar en historial
        findViewById<LinearLayout>(R.id.ButtonArchivarReabastecimientoDesdeVer).setOnClickListener {
            //Toast.makeText(this, "Archivar", Toast.LENGTH_LONG).show()
            showArchiveConfirmationDialog {
                archivarAbastecimiento()
            }
        }
    }

    // Ventana Modal de eliminar
    private fun showDeleteConfirmationDialog(onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_eliminar, null)
        val dialog = AlertDialog.Builder(this)
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

    // Metodo de eliminar
    private fun eliminarAbastecimiento(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("abastecimiento_medicamentos").document(documentId)

        // Eliminar el documento
        documentRef.delete()
            .addOnSuccessListener {
                // Manejo exitoso de la eliminación
                //val confirm = Intent(this, ConfirmacionEliminacionAbastecimiento::class.java)
                //startActivity(confirm)
            }
            .addOnFailureListener {
                // Manejo de errores al eliminar
            }
    }

    // Ventana Modal de archivar
    private fun showArchiveConfirmationDialog(onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_archivar_abastecimiento, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.btn_cancelar_abastecimiento).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_archivar_abastecimiento).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**/

    // Metodo de archivar
    private fun archivarAbastecimiento() {
        val db = FirebaseFirestore.getInstance()

        // Crear una referencia al documento de usuario
        val usuarioRef = db.document("/users/$userId")

        // Crear un mapa con los datos a registrar en Firestore
        val abastecimientoData = hashMapOf(
            "id_usuario" to usuarioRef,  // Usamos la referencia en lugar de solo el userId
            "medicamento" to abastecimiento_data.medicamento,
            "fecha_abastecimiento" to abastecimiento_data.fecha_abastecimiento,
            "cantidad" to abastecimiento_data.cantidad
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("archivar_abastecimiento")
            .add(abastecimientoData)  // Usa .add para generar un ID único automáticamente
            .addOnSuccessListener { documentReference ->
                // Mensaje de éxito
                //Toast.makeText(this, "Medicamento archivado con éxito: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                // lógica para eliminar el documento de la colección 'toma_medicamentos'
                abastecimiento_data.id_usuario?.let { eliminar(it) }
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar el abastecimiento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Este metodo va junto al metodo archivarActividad()
    private fun eliminar(abastecimientoId: String) {
        val db = FirebaseFirestore.getInstance()

        // Referencia al documento que se va a eliminar en la colección 'toma_medicamentos'
        val documentoRef = db.collection("abastecimiento_medicamentos").document(abastecimientoId)

        documentoRef.delete()
            .addOnSuccessListener {
                // Mensaje de éxito al eliminar
                //Toast.makeText(this, "Medicamento eliminado de la colección 'toma_medicamentos'.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Manejo de errores al eliminar
                //Toast.makeText(this, "Error al eliminar medicamento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    /**/

    // Metodo de eliminar
    private fun eliminarAbastecimientos(abastecimientotId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("abastecimiento_medicamentos").document(abastecimientotId)

        // Eliminar el documento
        documentRef.delete()
            .addOnSuccessListener {
                // Manejo exitoso de la eliminación
                startActivity(Intent(this, ConfirmacionEliminacionRecordatorioAbastecimiento::class.java))
            }
            .addOnFailureListener {
                // Manejo de errores al eliminar
            }
    }

    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        startActivity(Intent(this, ConfirmacionArchivoAbastecimientoMedicamento::class.java))
        finish() // Opcional: Cierra la actividad actual si no quieres volver a ella
    }
}