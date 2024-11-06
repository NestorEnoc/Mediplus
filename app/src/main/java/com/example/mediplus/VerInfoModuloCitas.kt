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

object citas_data {
    var id_usuario: String? = null
    var especialidad: String? = null
    var fecha: String? = null
    var hora: String? = null
    var nombre_medico: String? = null
    var descripcion: String? = null
}

class VerInfoModuloCitas : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_info_modulo_citas)

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
            "id_usuario" to { citas_data.id_usuario = intent.getStringExtra("id_usuario") },
            "especialidad" to { citas_data.especialidad = intent.getStringExtra("especialidad") },
            "fecha" to { citas_data.fecha = intent.getStringExtra("fecha") },
            "hora" to { citas_data.hora = intent.getStringExtra("hora") },
            "nombre_medico" to { citas_data.nombre_medico = intent.getStringExtra("nombre_medico") },
            "descripcion" to { citas_data.descripcion = intent.getStringExtra("descripcion") }
        )

        // Asignar los datos del Intent a las variables globales
        extrasMap.forEach { it.value() }

        // Crear un Map que asocie las IDs de los TextViews con las variables globales
        val textViewMap = mapOf(
            R.id.txtEspecialidadInfo to citas_data.especialidad,
            R.id.txtFechaInfo to citas_data.fecha,
            R.id.txtHoraInfo to citas_data.hora,
            R.id.txtNombreDoctorInfo to citas_data.nombre_medico,
            R.id.txtDescripcionInfo to citas_data.descripcion
        )

        // Asignar los datos a los TextViews
        textViewMap.forEach { (viewId, value) ->
            findViewById<TextView>(viewId).text = value
        }


        // Accion del boton Editar
        findViewById<LinearLayout>(R.id.ButtonEditarCitaMedica).setOnClickListener {
            val intent = Intent(this, EditarModuloCitas::class.java).apply {
                putExtra("id_usuario", citas_data.id_usuario)
                putExtra("especialidad", citas_data.especialidad)
                putExtra("fecha", citas_data.fecha)
                putExtra("hora", citas_data.hora)
                putExtra("nombre_medico", citas_data.nombre_medico)
                putExtra("descripcion", citas_data.descripcion)
            }
            startActivity(intent)
        }

        // Accion del boton Eliminar
        findViewById<LinearLayout>(R.id.ButtonEliminarCitaMedica).setOnClickListener {
            showDeleteConfirmationDialog {
                if (citas_data.id_usuario != null) {
                    eliminarCitaMedica(citas_data.id_usuario!!)
                }
            }

        }

        // Accion del boton Archivar en historial
        findViewById<LinearLayout>(R.id.ButtonArchivarCitaMedica).setOnClickListener {
            showArchiveConfirmationDialog {
                archivarCitaMedica()
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
    private fun eliminarCitaMedica(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("toma_citas").document(documentId)

        // Eliminar el documento
        documentRef.delete()
            .addOnSuccessListener {
                // Manejo exitoso de la eliminación
                startActivity(Intent(this, ConfirmacionEliminacionRecordatorioCita::class.java))
            }
            .addOnFailureListener {
                // Manejo de errores al eliminar
            }
    }

    // Ventana Modal de archivar
    private fun showArchiveConfirmationDialog(onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_archivar_cita, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.btn_cancelar_cita).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_archivar_cita).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**/

    // Metodo de archivar
    private fun archivarCitaMedica() {
        val db = FirebaseFirestore.getInstance()

        // Crear una referencia al documento de usuario
        val usuarioRef = db.document("/users/$userId")

        // Crear un mapa con los datos a registrar en Firestore
        val examenesData = hashMapOf(
            "id_usuario" to usuarioRef,
            "especialidad" to citas_data.especialidad,
            "fecha" to citas_data.fecha,
            "hora" to citas_data.hora,
            "nombre_medico" to citas_data.nombre_medico,
            "descripcion" to citas_data.descripcion
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("archivar_citas")
            .add(examenesData)  // Usa .add para generar un ID único automáticamente
            .addOnSuccessListener { documentReference ->
                // Mensaje de éxito
                //Toast.makeText(this, "Medicamento archivado con éxito: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                // lógica para eliminar el documento de la colección 'toma_medicamentos'
                citas_data.id_usuario?.let { eliminarCitaDespues(it) }
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al archivar la cita: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Este metodo va junto al metodo archivarMedicamento()
    private fun eliminarCitaDespues(examenId: String) {
        val db = FirebaseFirestore.getInstance()

        // Referencia al documento que se va a eliminar en la colección 'toma_medicamentos'
        val documentoRef = db.collection("toma_citas").document(examenId)

        documentoRef.delete()
            .addOnSuccessListener {
                // Mensaje de éxito al eliminar
                //Toast.makeText(this, "Examen eliminado de la colección 'examenes'.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Manejo de errores al eliminar
                //Toast.makeText(this, "Error al eliminar el examen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    /**/

    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        startActivity(Intent(this, ConfirmacionArchivoCita::class.java))
        finish()
    }
}