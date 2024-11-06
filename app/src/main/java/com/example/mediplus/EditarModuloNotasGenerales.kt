package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditarModuloNotasGenerales : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_modulo_notas_generales)

        /* Codigo para traer las iniciales del usuario logueado*/
        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
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

        // Obtener los datos del Intent
        val id_usuario = intent.getStringExtra("id_usuario")
        val titulo = intent.getStringExtra("titulo")
        val cuerpo_nota = intent.getStringExtra("cuerpo_nota")

        // Asignar los datos a los TextViews
        findViewById<TextView>(R.id.txtTituloNotaGeneralEditar).text = titulo
        findViewById<TextView>(R.id.txtDescripcionNotaGeneralEditar).text = cuerpo_nota

        // Accion del boton Guardar Recordatorio
        findViewById<LinearLayout>(R.id.btnEditarNotaGeneral).setOnClickListener {
            if (id_usuario != null) {
                if (validarCampos()) {
                    updateNotaGeneral(id_usuario)
                }
            }else{
                Toast.makeText(this, "Error: ${id_usuario} no encontrado", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun updateNotaGeneral(notaId: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            val tituloActualizada = findViewById<EditText>(R.id.txtTituloNotaGeneralEditar).text.toString()
            val cuerpoNotaActualizada = findViewById<EditText>(R.id.txtDescripcionNotaGeneralEditar).text.toString()

            if (notaId.isEmpty()) {
                Toast.makeText(this, "ID de la nota no válido", Toast.LENGTH_SHORT).show()
                return
            }

            // Obtener la fecha y hora actual
            val fechaActual = Date()

            // Formato para la fecha
            val formatoFecha = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val fechaFormateada = formatoFecha.format(fechaActual)

            // Formato para la hora
            val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val horaFormateada = formatoHora.format(fechaActual)

            // Crear el mapa con los datos a actualizar en Firestore, incluyendo la fecha y hora
            val notaMedicamentoActualizado = hashMapOf(
                "titulo" to tituloActualizada,
                "cuerpo_nota" to cuerpoNotaActualizada,
                "fecha" to fechaFormateada,
                "hora" to horaFormateada
            )

            db.collection("notas_generales").document(notaId)
                .set(notaMedicamentoActualizado, SetOptions.merge())
                .addOnSuccessListener {
                    redirectToConfirmation()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar la nota: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        startActivity(Intent(this, ConfirmacionEdicionRecordatorioNotaGeneral::class.java))
        finish()
    }

    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val titulo = findViewById<EditText>(R.id.txtTituloNotaGeneralEditar).text.toString().trim()
        val cuerpo_nota = findViewById<EditText>(R.id.txtDescripcionNotaGeneralEditar).text.toString().trim()

        var isValid = true

        // Validación para la fecha
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un titulo para editar la nota.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validación para la cantidad
        if (cuerpo_nota.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un texto para editar la nota.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }
}