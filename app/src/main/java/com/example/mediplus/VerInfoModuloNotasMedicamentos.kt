package com.example.mediplus

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

object notas_examenes {
    var id_usuario: String? = null
    var medicamento: String? = null
    var fecha: String? = null
    var hora: String? = null
    var titulo: String? = null
    var cuerpo_nota: String? = null
}

class VerInfoModuloNotasMedicamentos : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_info_modulo_notas_medicamentos)

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
            "id_usuario" to { notas_examenes.id_usuario = intent.getStringExtra("id_usuario") },
            "medicamento" to { notas_examenes.medicamento = intent.getStringExtra("medicamento") },
            "fecha" to { notas_examenes.fecha = intent.getStringExtra("fecha") },
            "hora" to { notas_examenes.hora = intent.getStringExtra("hora") },
            "titulo" to { notas_examenes.titulo = intent.getStringExtra("titulo") },
            "cuerpo_nota" to { notas_examenes.cuerpo_nota = intent.getStringExtra("cuerpo_nota") }
        )

        // Asignar los datos del Intent a las variables globales
        extrasMap.forEach { it.value() }

        // Crear un Map que asocie las IDs de los TextViews con las variables globales
        val textViewMap = mapOf(
            R.id.txtInfoMedicamento to notas_examenes.medicamento,
            R.id.txtInfoHoraFecha to "${notas_examenes.fecha} ${notas_examenes.hora}",
            R.id.txtCuerpoNotaInfo to notas_examenes.cuerpo_nota,
            //R.id.txtTituloNotaMedicamentoInfo to notas_examenes.titulo
        )

        // Asignar los datos a los TextViews
        textViewMap.forEach { (viewId, value) ->
            findViewById<TextView>(viewId).text = value
        }
    }
}