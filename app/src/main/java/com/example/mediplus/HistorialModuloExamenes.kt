package com.example.mediplus

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.HistorialExamenes
import com.example.mediplus.adapter.HistorialExamenesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HistorialModuloExamenes : AppCompatActivity() {

    private lateinit var examenesHistorialRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String
    private var listaExamenesFiltrados: List<HistorialExamenes> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_modulo_examenes)

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

        // Inicializa las vistas para visualizar los datos listados
        examenesHistorialRecyclerView = findViewById(R.id.examenesHistorialRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosViewExamenesHistorial)

        // Configura el RecyclerView
        examenesHistorialRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosHistorialExamenes()

        /**/

        val etFiltroMedicamentos = findViewById<EditText>(R.id.txtFiltroExamenes)
        val btnBuscarMedicamento = findViewById<LinearLayout>(R.id.btnBuscarExamenes)

        btnBuscarMedicamento.setOnClickListener {
            val palabraFiltrada = etFiltroMedicamentos.text.toString()
            filtrarExamenes(palabraFiltrada)
        }
    }

    private fun obtenerDatosHistorialExamenes() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("archivar_examenes")
            .whereEqualTo("id_usuario", id_usuario)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
                    mostrarMensajeSinDatos()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    mostrarExamenes(snapshot)
                } else {
                    //Toast.makeText(this, "No se encontraron documentos", Toast.LENGTH_LONG).show() <!-- Ultimo-->
                    mostrarMensajeSinDatos()
                }
            }
    }


    private fun mostrarExamenes(documentos: QuerySnapshot) {
        val listaHistorialExamenes = documentos.map { doc ->

            idDocumento = doc.id

            HistorialExamenes(
                id_usuario = idDocumento,
                nombre_examen = doc.getString("nombre_examen") ?: "Sin nombre",
                fecha = doc.getString("fecha") ?: "Sin fecha",
                hora = doc.getString("hora") ?: "Sin hora",
                especialidad = doc.getString("especialidad") ?: "Sin especialidad"
            )
        }

        // Inicializa el RecyclerView con la lista original
        listaExamenesFiltrados = listaHistorialExamenes
        // Configura el adaptador del RecyclerView
        examenesHistorialRecyclerView.adapter = HistorialExamenesAdapter(listaExamenesFiltrados)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        examenesHistorialRecyclerView.visibility = View.VISIBLE
    }

    private fun filtrarExamenes(palabra: String) {
        val listaFiltrada = listaExamenesFiltrados.filter { historial ->
            historial.nombre_examen.contains(palabra, ignoreCase = true) ||
            historial.fecha.contains(palabra, ignoreCase = true) ||
            historial.hora.contains(palabra, ignoreCase = true)
            historial.especialidad.contains(palabra, ignoreCase = true)
        }
        // Actualiza el adaptador con la lista filtrada
        examenesHistorialRecyclerView.adapter = HistorialExamenesAdapter(listaFiltrada)
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        examenesHistorialRecyclerView.visibility = View.GONE
    }
}