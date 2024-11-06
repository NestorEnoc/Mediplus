package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrearModuloNotasMedicamentos : AppCompatActivity() {

    private lateinit var userId: String
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_modulo_notas_medicamentos)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        /* Codigo para traer las iniciales del usuario logueado*/
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

        val btnRegistrar = findViewById<LinearLayout>(R.id.btnCrearNotaMedicamentos)
        btnRegistrar.setOnClickListener {
            if (validarCampos()) {
                registrarNotaMedicamento()
            }

        }

        // Para llamar y obtener datos en select
        val txtFiltroMedicamentos = findViewById<AutoCompleteTextView>(R.id.txtFiltroMedicamentos)
        txtFiltroMedicamentos.setOnClickListener {
            if (!txtFiltroMedicamentos.isPopupShowing) {
                txtFiltroMedicamentos.showDropDown()
            }
        }
        obtenerMedicamento()

    }

    private fun registrarNotaMedicamento() {
        val db = FirebaseFirestore.getInstance()

        // Obtener los nuevos datos de los EditText
        val medicamento = findViewById<EditText>(R.id.txtFiltroMedicamentos).text.toString()
        val titulo = findViewById<EditText>(R.id.txtTituloNotaMedicamento).text.toString()
        val cuerpo_nota = findViewById<EditText>(R.id.txtDescripcionGestionSalud).text.toString()

        // Crear una referencia al documento de usuario
        val usuarioRef = db.document("/users/$userId")

        // Obtener la fecha y hora actual
        val fechaActual = Date()

        // Formato para la fecha
        val formatoFecha = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val fechaFormateada = formatoFecha.format(fechaActual)

        // Formato para la hora
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val horaFormateada = formatoHora.format(fechaActual)

        // Crear un mapa con los datos a registrar en Firestore
        val medicamentoData = hashMapOf(
            "id_usuario" to usuarioRef,
            "medicamento" to medicamento,
            "titulo" to titulo,
            "cuerpo_nota" to cuerpo_nota,
            "fecha" to fechaFormateada,
            "hora" to horaFormateada
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("notas_medicamentos")
            .add(medicamentoData)  // Usa .add para generar un ID único automáticamente
            .addOnSuccessListener { documentReference ->
                // Redirigir a la actividad de confirmación
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar medicamento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        startActivity(Intent(this, ConfirmacionRecordatorioNotasMedicamento::class.java))
        finish()
    }

    // Metodo para obtener datos en select
    private fun obtenerMedicamento() {
        val medicamentoList = mutableListOf<String>()  // Lista para almacenar los nombres de medicamentos

        val usuarioRef = db.document("/users/$userId")

        val db = FirebaseFirestore.getInstance()
        db.collection("toma_medicamentos")
            .whereEqualTo("id_usuario", usuarioRef)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val nombreMedicamento = document.getString("nombre_medicamento") ?: "Sin nombre"
                    medicamentoList.add(nombreMedicamento)  // Agrega cada nombre a la lista
                }

                // Asigna los datos a txtFiltroMedicamentos
                val txtFiltroMedicamentos = findViewById<AutoCompleteTextView>(R.id.txtFiltroMedicamentos)
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, medicamentoList)
                txtFiltroMedicamentos.setAdapter(adapter)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val titulo = findViewById<EditText>(R.id.txtTituloNotaMedicamento).text.toString().trim()
        val medicamento = findViewById<AutoCompleteTextView>(R.id.txtFiltroMedicamentos).text.toString().trim()
        val cuerpo_nota = findViewById<EditText>(R.id.txtDescripcionGestionSalud).text.toString().trim()

        var isValid = true

        // Validación para el medicamento
        if (medicamento.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un medicamento o crear uno en el módulo de medicamentos.", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (medicamento == "Seleccione...") {
            Toast.makeText(this, "Debe seleccionar un medicamento o crear uno en el módulo de medicamentos.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validación para la fecha
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un titulo para la nota.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validación para la cantidad
        if (cuerpo_nota.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un texto para crear la nota.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }


}