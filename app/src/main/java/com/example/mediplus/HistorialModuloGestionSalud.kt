package com.example.mediplus

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.HistorialGestionSalud
import com.example.mediplus.adapter.HistorialGestionSaludAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HistorialModuloGestionSalud : AppCompatActivity() {

    private lateinit var gestionSaludHistorialRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String
    private var listaGestionSaludFiltrados: List<HistorialGestionSalud> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_modulo_gestion_salud)

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
        gestionSaludHistorialRecyclerView = findViewById(R.id.gestionSaludHistorialRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosViewGestionSaludHistorial)

        // Configura el RecyclerView
        gestionSaludHistorialRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosHistorialGestionSalud()

        /**/

        val etFiltroMedicamentos = findViewById<EditText>(R.id.txtFiltroGestionSalud)
        val btnBuscarMedicamento = findViewById<LinearLayout>(R.id.btnBuscarGestionSalud)

        btnBuscarMedicamento.setOnClickListener {
            val palabraFiltrada = etFiltroMedicamentos.text.toString()
            filtrarAbastecimiento(palabraFiltrada)
        }
    }

    private fun obtenerDatosHistorialGestionSalud() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("archivar_gestion_salud")
            .whereEqualTo("id_usuario", id_usuario)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
                    mostrarMensajeSinDatos()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    mostrarAbastecimientoGestionSalud(snapshot)
                } else {
                    //Toast.makeText(this, "No se encontraron documentos", Toast.LENGTH_LONG).show() <!-- Ultimo-->
                    mostrarMensajeSinDatos()
                }
            }
    }


    private fun mostrarAbastecimientoGestionSalud(documentos: QuerySnapshot) {
        val listaHistorialGestionSalud = documentos.map { doc ->

            idDocumento = doc.id

            HistorialGestionSalud(
                id_usuario = idDocumento,
                enfermedad = doc.getString("enfermedad") ?: "Sin nombre",
                fecha = doc.getString("fecha") ?: "Sin fecha",
                hora = doc.getString("hora") ?: "Sin hora",
                categoria = doc.getString("categoria") ?: "Sin categoria",
                descripcion = doc.getString("descripcion") ?: "Sin descripcion"
            )
        }

        // Inicializa el RecyclerView con la lista original
        listaGestionSaludFiltrados = listaHistorialGestionSalud
        // Configura el adaptador del RecyclerView
        gestionSaludHistorialRecyclerView.adapter = HistorialGestionSaludAdapter(listaGestionSaludFiltrados)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        gestionSaludHistorialRecyclerView.visibility = View.VISIBLE
    }

    private fun filtrarAbastecimiento(palabra: String) {
        val listaFiltrada = listaGestionSaludFiltrados.filter { historial ->
            historial.enfermedad.contains(palabra, ignoreCase = true) ||
            historial.fecha.contains(palabra, ignoreCase = true) ||
            historial.hora.contains(palabra, ignoreCase = true)
            historial.categoria.contains(palabra, ignoreCase = true)
            historial.descripcion.contains(palabra, ignoreCase = true)
        }
        // Actualiza el adaptador con la lista filtrada
        gestionSaludHistorialRecyclerView.adapter = HistorialGestionSaludAdapter(listaFiltrada)
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        gestionSaludHistorialRecyclerView.visibility = View.GONE
    }
}