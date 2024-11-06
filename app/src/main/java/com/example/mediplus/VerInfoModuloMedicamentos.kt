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

object Globals {
    var id_usuario: String? = null
    var nombre_medicamento: String? = null
    var fecha_desde: String? = null
    var num_dosis: String? = null
    var descripcion: String? = null
    var fecha_hasta: String? = null
    var hora: String? = null
    var hora_mas_veces: String? = null
    var intervalo_horas: String? = null
    var presentacion: String? = null
    var unidad: String? = null
}

class VerInfoModuloMedicamentos : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_info_modulo_medicamentos)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        // Obtener los datos del Intent
        /*val id_usuario = intent.getStringExtra("id_usuario")
        val nombre_medicamento = intent.getStringExtra("nombre_medicamento")
        val fecha_desde = intent.getStringExtra("fecha_desde")
        val num_dosis = intent.getStringExtra("num_dosis")
        val descripcion = intent.getStringExtra("descripcion")
        val fecha_hasta = intent.getStringExtra("fecha_hasta")
        val hora = intent.getStringExtra("hora")
        val hora_mas_veces = intent.getStringExtra("hora_mas_veces")
        val intervalo_horas = intent.getStringExtra("intervalo_horas")
        val presentacion = intent.getStringExtra("presentacion")
        val unidad = intent.getStringExtra("unidad")

        // Asignar los datos a los TextViews para ver info medicamentos
        findViewById<TextView>(R.id.txtDetallesMedicamento).text = nombre_medicamento
        findViewById<TextView>(R.id.txtDetallesFechaDesde).text = fecha_desde
        findViewById<TextView>(R.id.txtDetallesDosis).text = num_dosis
        findViewById<TextView>(R.id.txtDetallesDescripcion).text = descripcion
        findViewById<TextView>(R.id.txtDetallesFechaHasta).text = fecha_hasta
        findViewById<TextView>(R.id.txtDetallesPresentacion).text = presentacion
        findViewById<TextView>(R.id.txtDetallesUnidad).text = unidad*/

        // Crear un Map que asocie las claves de los extras con las variables globales
        val extrasMap = mapOf(
            "id_usuario" to { Globals.id_usuario = intent.getStringExtra("id_usuario") },
            "nombre_medicamento" to { Globals.nombre_medicamento = intent.getStringExtra("nombre_medicamento") },
            "fecha_desde" to { Globals.fecha_desde = intent.getStringExtra("fecha_desde") },
            "num_dosis" to { Globals.num_dosis = intent.getStringExtra("num_dosis") },
            "descripcion" to { Globals.descripcion = intent.getStringExtra("descripcion") },
            "fecha_hasta" to { Globals.fecha_hasta = intent.getStringExtra("fecha_hasta") },
            "hora" to { Globals.hora = intent.getStringExtra("hora") },
            "hora_mas_veces" to { Globals.hora_mas_veces = intent.getStringExtra("hora_mas_veces") },
            "intervalo_horas" to { Globals.intervalo_horas = intent.getStringExtra("intervalo_horas") },
            "presentacion" to { Globals.presentacion = intent.getStringExtra("presentacion") },
            "unidad" to { Globals.unidad = intent.getStringExtra("unidad") }
        )

        // Asignar los datos del Intent a las variables globales
        extrasMap.forEach { it.value() }

        // Crear un Map que asocie las IDs de los TextViews con las variables globales
        val textViewMap = mapOf(
            R.id.txtDetallesMedicamento to Globals.nombre_medicamento,
            R.id.txtDetallesFechaDesde to Globals.fecha_desde,
            R.id.txtDetallesDosis to Globals.num_dosis,
            R.id.txtDetallesDescripcion to Globals.descripcion,
            R.id.txtDetallesFechaHasta to Globals.fecha_hasta,
            R.id.txtDetallesPresentacion to Globals.presentacion,
            R.id.txtDetallesUnidad to Globals.unidad
        )

        // Asignar los datos a los TextViews
        textViewMap.forEach { (viewId, value) ->
            findViewById<TextView>(viewId).text = value
        }


        // Accion del boton Editar
        findViewById<LinearLayout>(R.id.ButtonEditar).setOnClickListener {
            val intent = Intent(this, EditarModuloMedicamentos::class.java).apply {
                putExtra("id_usuario", Globals.id_usuario)
                putExtra("nombre_medicamento", Globals.nombre_medicamento)
                putExtra("fecha_desde", Globals.fecha_desde)
                putExtra("num_dosis", Globals.num_dosis)
                putExtra("descripcion", Globals.descripcion)
                putExtra("fecha_hasta", Globals.fecha_hasta)
                putExtra("hora", Globals.hora)
                putExtra("hora_mas_veces", Globals.hora_mas_veces)
                putExtra("intervalo_horas", Globals.intervalo_horas)
                putExtra("presentacion", Globals.presentacion)
                putExtra("unidad", Globals.unidad)
            }
            startActivity(intent)
        }

        // Accion del boton Eliminar
        findViewById<LinearLayout>(R.id.ButtonEliminar).setOnClickListener {
            //Toast.makeText(this, "Eliminar", Toast.LENGTH_LONG).show()
            showDeleteConfirmationDialog {
                // Eliminar el medicamento de Firestore
                if (Globals.id_usuario != null) {
                    eliminarMedicamentoVerInfo(Globals.id_usuario!!)
                }
            }

        }

        // Accion del boton Archivar en historial
        findViewById<LinearLayout>(R.id.ButtonArchivar).setOnClickListener {
            //Toast.makeText(this, "Archivar", Toast.LENGTH_LONG).show()
            showArchiveConfirmationDialog {
                archivarMedicamento()
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
    private fun eliminarMedicamentoVerInfo(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("toma_medicamentos").document(documentId)

        // Eliminar el documento
        documentRef.delete()
            .addOnSuccessListener {
                // Manejo exitoso de la eliminación
                val confirm = Intent(this, ConfirmacionEliminacionRecordatorioMedicamento::class.java)
                startActivity(confirm)
            }
            .addOnFailureListener {
                // Manejo de errores al eliminar
            }
    }

    // Ventana Modal de archivar
    private fun showArchiveConfirmationDialog(onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_archivar_modulo_medicamentos, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.btn_cancelar_medicamentos).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_archivar_medicamentos).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**/

    // Metodo de archivar
    private fun archivarMedicamento() {
        val db = FirebaseFirestore.getInstance()

        // Crear una referencia al documento de usuario
        val usuarioRef = db.document("/users/$userId")

        // Crear un mapa con los datos a registrar en Firestore
        val medicamentoData = hashMapOf(
            "id_usuario" to usuarioRef,  // Usamos la referencia en lugar de solo el userId
            "nombre_medicamento" to Globals.nombre_medicamento,
            "presentacion" to Globals.presentacion,
            "fecha_desde" to Globals.fecha_desde,
            "fecha_hasta" to Globals.fecha_hasta,
            "num_dosis" to Globals.num_dosis,
            "unidad" to Globals.unidad,
            "hora" to Globals.hora,
            "intervalo_horas" to Globals.intervalo_horas,
            "hora_mas_veces" to Globals.hora_mas_veces,
            "descripcion" to Globals.descripcion,
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("archivar_medicamentos")
            .add(medicamentoData)  // Usa .add para generar un ID único automáticamente
            .addOnSuccessListener { documentReference ->
                // Mensaje de éxito
                //Toast.makeText(this, "Medicamento archivado con éxito: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                // lógica para eliminar el documento de la colección 'toma_medicamentos'
                Globals.id_usuario?.let { eliminarMedicamentoDeToma(it) }
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar medicamento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Este metodo va junto al metodo archivarMedicamento()
    private fun eliminarMedicamentoDeToma(medicamentoId: String) {
        val db = FirebaseFirestore.getInstance()

        // Referencia al documento que se va a eliminar en la colección 'toma_medicamentos'
        val documentoRef = db.collection("toma_medicamentos").document(medicamentoId)

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
        startActivity(Intent(this, ConfirmacionArchivoRecordatorioMedicamento::class.java))
        finish()
    }
}