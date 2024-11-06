package com.example.mediplus

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.HistorialVidaSaludableAdapter
import com.example.mediplus.adapter.VidaSaludable
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HistorialModuloVidaSaludable : AppCompatActivity() {

    private lateinit var vidaSaludableHistorialRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String
    private var listaActividadesFiltrados: List<VidaSaludable> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_vida_saludable)

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
        vidaSaludableHistorialRecyclerView = findViewById(R.id.vidaSaludableHistorialRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosViewVidaSaludableHistorial)

        // Configura el RecyclerView
        vidaSaludableHistorialRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosHistorialVidaSaludable()

        /**/

        val etFiltroMedicamentos = findViewById<EditText>(R.id.txtFiltroVidaSaludable)
        val btnBuscarMedicamento = findViewById<LinearLayout>(R.id.btnBuscarActividades)

        btnBuscarMedicamento.setOnClickListener {
            val palabraFiltrada = etFiltroMedicamentos.text.toString()
            filtrarMedicamentos(palabraFiltrada) // Llama al método de filtrado
        }
    }

    private fun obtenerDatosHistorialVidaSaludable() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("archivar_vida_saludable")
            .whereEqualTo("id_usuario", id_usuario)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
                    mostrarMensajeSinDatos()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    mostrarMedicamentos(snapshot)
                } else {
                    //Toast.makeText(this, "No se encontraron documentos", Toast.LENGTH_LONG).show() <!-- Ultimo-->
                    mostrarMensajeSinDatos()
                }
            }
    }


    private fun mostrarMedicamentos(documentos: QuerySnapshot) {
        val listaHistorialVidaSaludable = documentos.map { doc ->

            idDocumento = doc.id

            VidaSaludable(
                id_usuario = idDocumento,
                actividad = doc.getString("actividad") ?: "Sin actividad",
                categoria = doc.getString("categoria") ?: "Sin categoria",
                descripcion = doc.getString("descripcion") ?: "Sin descripcion",
                fecha = doc.getString("fecha")?: "Sin fecha",
                hora = doc.getString("hora")?: "Sin hora"
            )
        }

        // Inicializa el RecyclerView con la lista original
        listaActividadesFiltrados = listaHistorialVidaSaludable
        // Configura el adaptador del RecyclerView
        vidaSaludableHistorialRecyclerView.adapter = HistorialVidaSaludableAdapter(listaActividadesFiltrados)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        vidaSaludableHistorialRecyclerView.visibility = View.VISIBLE
    }

    private fun filtrarMedicamentos(palabra: String) {
        val listaFiltrada = listaActividadesFiltrados.filter { historial ->
            historial.actividad.contains(palabra, ignoreCase = true) ||
            historial.categoria.contains(palabra, ignoreCase = true) ||
            historial.descripcion.contains(palabra, ignoreCase = true) ||
            historial.fecha.contains(palabra, ignoreCase = true) ||
            historial.hora.contains(palabra, ignoreCase = true)
        }
        // Actualiza el adaptador con la lista filtrada
        vidaSaludableHistorialRecyclerView.adapter = HistorialVidaSaludableAdapter(listaFiltrada)
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        vidaSaludableHistorialRecyclerView.visibility = View.GONE
    }
}