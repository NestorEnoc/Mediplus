package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrearModuloNotasGenerales : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_modulo_notas_generales)

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

        val btnRegistrar = findViewById<LinearLayout>(R.id.btnCrearNotaGeneral)
        btnRegistrar.setOnClickListener {
            if (validarCampos()) {
                registrarNotaGeneral()
            }
        }
    }

    private fun registrarNotaGeneral() {
        val db = FirebaseFirestore.getInstance()

        // Obtener los nuevos datos de los EditText
        val titulo = findViewById<EditText>(R.id.txtTituloNotaGeneralRegistrar).text.toString()
        val cuerpo_nota = findViewById<EditText>(R.id.txtDescripcionNotaGeneralRegistrar).text.toString()

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
            "titulo" to titulo,
            "cuerpo_nota" to cuerpo_nota,
            "fecha" to fechaFormateada,
            "hora" to horaFormateada
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("notas_generales")
            .add(medicamentoData)
            .addOnSuccessListener { documentReference ->
                // Redirigir a la actividad de confirmación
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar la nota: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        startActivity(Intent(this, ConfirmacionRecordatorioNotasGenerales::class.java))
        finish()
    }

    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val titulo = findViewById<EditText>(R.id.txtTituloNotaGeneralRegistrar).text.toString().trim()
        val cuerpo_nota = findViewById<EditText>(R.id.txtDescripcionNotaGeneralRegistrar).text.toString().trim()

        var isValid = true

        // Validación para la fecha
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un titulo para crear la nota.", Toast.LENGTH_SHORT).show()
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