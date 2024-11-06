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

object examenes_data {
    var id_usuario: String? = null
    var nombre_examen: String? = null
    var fecha: String? = null
    var hora: String? = null
    var especialidad: String? = null
    var entidad: String? = null
    var nombre_doctor: String? = null
    var descripcion: String? = null
}

class VerInfoModuloExamenes : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_info_modulo_examenes)

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
            "id_usuario" to { examenes_data.id_usuario = intent.getStringExtra("id_usuario") },
            "nombre_examen" to { examenes_data.nombre_examen = intent.getStringExtra("nombre_examen") },
            "fecha" to { examenes_data.fecha = intent.getStringExtra("fecha") },
            "hora" to { examenes_data.hora = intent.getStringExtra("hora") },
            "especialidad" to { examenes_data.especialidad = intent.getStringExtra("especialidad") },
            "entidad" to { examenes_data.entidad = intent.getStringExtra("entidad") },
            "nombre_doctor" to { examenes_data.nombre_doctor = intent.getStringExtra("nombre_doctor") },
            "descripcion" to { examenes_data.descripcion = intent.getStringExtra("descripcion") }
        )

        // Asignar los datos del Intent a las variables globales
        extrasMap.forEach { it.value() }

        // Crear un Map que asocie las IDs de los TextViews con las variables globales
        val textViewMap = mapOf(
            R.id.txtEnfermedadExamenes to examenes_data.nombre_examen,
            R.id.txtFechaExamenes to examenes_data.fecha,
            R.id.txtHoraExamenes to examenes_data.hora,
            R.id.txtEspecialidadExamenes to examenes_data.especialidad,
            R.id.txtEntidadExamenes to examenes_data.entidad,
            R.id.txtDoctorExamenes to examenes_data.nombre_doctor,
            R.id.txtDescripcionExamenes to examenes_data.descripcion
        )

        // Asignar los datos a los TextViews
        textViewMap.forEach { (viewId, value) ->
            findViewById<TextView>(viewId).text = value
        }


        // Accion del boton Editar
        findViewById<LinearLayout>(R.id.ButtonEditarExamenes).setOnClickListener {
            val intent = Intent(this, EditarModuloExamenes::class.java).apply {
                putExtra("id_usuario", examenes_data.id_usuario)
                putExtra("nombre_examen", examenes_data.nombre_examen)
                putExtra("fecha", examenes_data.fecha)
                putExtra("hora", examenes_data.hora)
                putExtra("especialidad", examenes_data.especialidad)
                putExtra("entidad", examenes_data.entidad)
                putExtra("nombre_doctor", examenes_data.nombre_doctor)
                putExtra("descripcion", examenes_data.descripcion)
            }
            startActivity(intent)
        }

        // Accion del boton Eliminar
        findViewById<LinearLayout>(R.id.ButtonEliminarExamenes).setOnClickListener {
            //Toast.makeText(this, "Eliminar", Toast.LENGTH_LONG).show()
            showDeleteConfirmationDialog {
                // Eliminar el medicamento de Firestore
                if (examenes_data.id_usuario != null) {
                    eliminarExamenes(examenes_data.id_usuario!!)
                }
            }

        }

        // Accion del boton Archivar en historial
        findViewById<LinearLayout>(R.id.ButtonArchivarExamenes).setOnClickListener {
            //Toast.makeText(this, "Archivar", Toast.LENGTH_LONG).show()
            showArchiveConfirmationDialog {
                archivarExamen()
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
    private fun eliminarExamenes(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("examenes").document(documentId)

        // Eliminar el documento
        documentRef.delete()
            .addOnSuccessListener {
                // Manejo exitoso de la eliminación
                startActivity(Intent(this, ConfirmacionEliminacionRecordatorioExamen::class.java))
            }
            .addOnFailureListener {
                // Manejo de errores al eliminar
            }
    }

    // Ventana Modal de archivar
    private fun showArchiveConfirmationDialog(onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_archivar_examenes, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.btn_cancelar_examenes).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_archivar_examenes).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**/

    // Metodo de archivar
    private fun archivarExamen() {
        val db = FirebaseFirestore.getInstance()

        // Crear una referencia al documento de usuario
        val usuarioRef = db.document("/users/$userId")

        // Crear un mapa con los datos a registrar en Firestore
        val examenesData = hashMapOf(
            "id_usuario" to usuarioRef,  // Usamos la referencia en lugar de solo el userId
            "nombre_examen" to examenes_data.nombre_examen,
            "fecha" to examenes_data.fecha,
            "hora" to examenes_data.hora,
            "especialidad" to examenes_data.especialidad,
            "entidad" to examenes_data.entidad,
            "nombre_doctor" to examenes_data.nombre_doctor,
            "descripcion" to examenes_data.descripcion
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("archivar_examenes")
            .add(examenesData)  // Usa .add para generar un ID único automáticamente
            .addOnSuccessListener { documentReference ->
                // Mensaje de éxito
                //Toast.makeText(this, "Medicamento archivado con éxito: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                // lógica para eliminar el documento de la colección 'toma_medicamentos'
                examenes_data.id_usuario?.let { eliminarExamenDeToma(it) }
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al archivar el examen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Este metodo va junto al metodo archivarMedicamento()
    private fun eliminarExamenDeToma(examenId: String) {
        val db = FirebaseFirestore.getInstance()

        // Referencia al documento que se va a eliminar en la colección 'toma_medicamentos'
        val documentoRef = db.collection("examenes").document(examenId)

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
        startActivity(Intent(this, ConfirmacionArchivoExamen::class.java))
        finish()
    }
}