package com.example.mediplus

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object notas_generales {
    var id_usuario: String? = null
    var fecha: String? = null
    var hora: String? = null
    var titulo: String? = null
    var cuerpo_nota: String? = null
}

class VerInfoModuloNotasGenerales : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_info_modulo_notas_generales)

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
            "id_usuario" to { notas_generales.id_usuario = intent.getStringExtra("id_usuario") },
            "fecha" to { notas_generales.fecha = intent.getStringExtra("fecha") },
            "hora" to { notas_generales.hora = intent.getStringExtra("hora") },
            "titulo" to { notas_generales.titulo = intent.getStringExtra("titulo") },
            "cuerpo_nota" to { notas_generales.cuerpo_nota = intent.getStringExtra("cuerpo_nota") }
        )

        // Asignar los datos del Intent a las variables globales
        extrasMap.forEach { it.value() }

        // Crear un Map que asocie las IDs de los TextViews con las variables globales
        val textViewMap = mapOf(
            R.id.txtInfoHoraFechaGeneral to "${notas_generales.fecha} ${notas_generales.hora}",
            R.id.txtCuerpoNotaInfoGeneral to notas_generales.cuerpo_nota,
        )

        // Asignar los datos a los TextViews
        textViewMap.forEach { (viewId, value) ->
            findViewById<TextView>(viewId).text = value
        }
    }
}