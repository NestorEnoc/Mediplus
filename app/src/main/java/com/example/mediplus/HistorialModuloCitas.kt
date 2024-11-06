package com.example.mediplus

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.HistorialCitas
import com.example.mediplus.adapter.HistorialCitasAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HistorialModuloCitas : AppCompatActivity() {

    private lateinit var citasHistorialRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String
    private var listaCitasFiltrados: List<HistorialCitas> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_modulo_citas)

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
        citasHistorialRecyclerView = findViewById(R.id.citasHistorialRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosViewCitasHistorial)

        // Configura el RecyclerView
        citasHistorialRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosHistorialExamenes()

        /**/

        val etFiltroMedicamentos = findViewById<EditText>(R.id.txtFiltroCitas)
        val btnBuscarMedicamento = findViewById<LinearLayout>(R.id.btnBuscarCitas)

        btnBuscarMedicamento.setOnClickListener {
            val palabraFiltrada = etFiltroMedicamentos.text.toString()
            filtrarCitas(palabraFiltrada)
        }
    }

    private fun obtenerDatosHistorialExamenes() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("archivar_citas")
            .whereEqualTo("id_usuario", id_usuario)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
                    mostrarMensajeSinDatos()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    mostrarCitas(snapshot)
                } else {
                    //Toast.makeText(this, "No se encontraron documentos", Toast.LENGTH_LONG).show() <!-- Ultimo-->
                    mostrarMensajeSinDatos()
                }
            }
    }


    private fun mostrarCitas(documentos: QuerySnapshot) {
        val listaHistorialCitas = documentos.map { doc ->

            idDocumento = doc.id

            HistorialCitas(
                id_usuario = idDocumento,
                especialidad = doc.getString("especialidad") ?: "Sin especialidad",
                fecha = doc.getString("fecha") ?: "Sin fecha",
                hora = doc.getString("hora") ?: "Sin hora",
                nombre_medico = doc.getString("nombre_medico") ?: "Sin nombre"
            )
        }

        // Inicializa el RecyclerView con la lista original
        listaCitasFiltrados = listaHistorialCitas
        // Configura el adaptador del RecyclerView
        citasHistorialRecyclerView.adapter = HistorialCitasAdapter(listaCitasFiltrados)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        citasHistorialRecyclerView.visibility = View.VISIBLE
    }

    private fun filtrarCitas(palabra: String) {
        val listaFiltrada = listaCitasFiltrados.filter { historial ->
            historial.especialidad.contains(palabra, ignoreCase = true) ||
            historial.fecha.contains(palabra, ignoreCase = true) ||
            historial.hora.contains(palabra, ignoreCase = true)
            historial.nombre_medico.contains(palabra, ignoreCase = true)
        }
        // Actualiza el adaptador con la lista filtrada
        citasHistorialRecyclerView.adapter = HistorialCitasAdapter(listaFiltrada)
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        citasHistorialRecyclerView.visibility = View.GONE
    }
}