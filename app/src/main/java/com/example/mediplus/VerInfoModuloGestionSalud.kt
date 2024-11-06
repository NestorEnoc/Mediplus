package com.example.mediplus

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

object gestion_salud {
    var id_usuario: String? = null
    var enfermedad: String? = null
    var categoria: String? = null
    var descripcion: String? = null
    var fecha: String? = null
    var hora: String? = null
}

class VerInfoModuloGestionSalud : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_info_modulo_gestion_salud)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        // Crear un Map que asocie las claves de los extras con las variables globales
        val extrasMap = mapOf(
            "id_usuario" to { gestion_salud.id_usuario = intent.getStringExtra("id_usuario") },
            "enfermedad" to { gestion_salud.enfermedad = intent.getStringExtra("enfermedad") },
            "categoria" to { gestion_salud.categoria = intent.getStringExtra("categoria") },
            "descripcion" to { gestion_salud.descripcion = intent.getStringExtra("descripcion") },
            "fecha" to { gestion_salud.fecha = intent.getStringExtra("fecha") },
            "hora" to { gestion_salud.hora = intent.getStringExtra("hora") }
        )

        // Asignar los datos del Intent a las variables globales
        extrasMap.forEach { it.value() }

        // Crear un Map que asocie las IDs de los TextViews con las variables globales
        val textViewMap = mapOf(
            R.id.txtDetallesEnfermedadGestionSalud to gestion_salud.enfermedad,
            R.id.txtDetallesFechaGestionSalud to gestion_salud.fecha,
            R.id.txtDetallesHoraGestionSalud to gestion_salud.hora,
            R.id.txtDetallesCategoriaGestionSalud to gestion_salud.categoria,
            R.id.txtDetallesDescripcionGestionSalud to gestion_salud.descripcion
        )

        // Asignar los datos a los TextViews
        textViewMap.forEach { (viewId, value) ->
            findViewById<TextView>(viewId).text = value
        }


        // Accion del boton Editar
        findViewById<LinearLayout>(R.id.ButtonEditarGestionSalud).setOnClickListener {
            val intent = Intent(this, EditarModuloGestionSalud::class.java).apply {
                putExtra("id_usuario", gestion_salud.id_usuario)
                putExtra("enfermedad", gestion_salud.enfermedad)
                putExtra("categoria", gestion_salud.categoria)
                putExtra("descripcion", gestion_salud.descripcion)
                putExtra("fecha", gestion_salud.fecha)
                putExtra("hora", gestion_salud.hora)
            }
            startActivity(intent)
        }

        // Accion del boton Eliminar
        findViewById<LinearLayout>(R.id.ButtonEliminarGestionSalud).setOnClickListener {
            //Toast.makeText(this, "Eliminar", Toast.LENGTH_LONG).show()
            showDeleteConfirmationDialog {
                // Eliminar el medicamento de Firestore
                if (gestion_salud.id_usuario != null) {
                    eliminarGestionSaludVerInfo(gestion_salud.id_usuario!!)
                }
            }

        }

        // Accion del boton Archivar en historial
        findViewById<LinearLayout>(R.id.ButtonArchivarGestionSalud).setOnClickListener {
            //Toast.makeText(this, "Archivar", Toast.LENGTH_LONG).show()
            showArchiveConfirmationDialog {
                archivarEnfermedad()
            }
        }

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
    private fun eliminarGestionSaludVerInfo(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("gestion_salud").document(documentId)

        // Eliminar el documento
        documentRef.delete()
            .addOnSuccessListener {
                // Manejo exitoso de la eliminación
                val confirm = Intent(this, ConfirmacionEliminacionRecordatorioGestionSalud::class.java)
                startActivity(confirm)
            }
            .addOnFailureListener {
                // Manejo de errores al eliminar
            }
    }

    // Ventana Modal de archivar
    private fun showArchiveConfirmationDialog(onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_archivar_modulo_gestion_salud, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.btn_cancelar_vida_saludable).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_archivar_vida_saludable).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**/

    // Metodo de archivar
    private fun archivarEnfermedad() {
        val db = FirebaseFirestore.getInstance()

        // Crear una referencia al documento de usuario
        val usuarioRef = db.document("/users/$userId")

        // Crear un mapa con los datos a registrar en Firestore
        val gestionSaludData = hashMapOf(
            "id_usuario" to usuarioRef,  // Usamos la referencia en lugar de solo el userId
            "enfermedad" to gestion_salud.enfermedad,
            "categoria" to gestion_salud.categoria,
            "descripcion" to gestion_salud.descripcion,
            "fecha" to gestion_salud.fecha,
            "hora" to gestion_salud.hora
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("archivar_gestion_salud")
            .add(gestionSaludData)  // Usa .add para generar un ID único automáticamente
            .addOnSuccessListener { documentReference ->
                // Mensaje de éxito
                //Toast.makeText(this, "Medicamento archivado con éxito: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                // lógica para eliminar el documento de la colección 'toma_medicamentos'
                gestion_salud.id_usuario?.let { eliminarEnfermedad(it) }
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar medicamento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Este metodo va junto al metodo archivarActividad()
    private fun eliminarEnfermedad(enfermedadId: String) {
        val db = FirebaseFirestore.getInstance()

        // Referencia al documento que se va a eliminar en la colección 'toma_medicamentos'
        val documentoRef = db.collection("gestion_salud").document(enfermedadId)

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

    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        startActivity(Intent(this, ConfirmacionArchivoGestionSalud::class.java))
        finish() // Opcional: Cierra la actividad actual si no quieres volver a ella
    }
}