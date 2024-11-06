package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.Citas
import com.example.mediplus.adapter.CitasAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ModuloCitas : AppCompatActivity() {

    private lateinit var citasRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo_citas)

        // Codigo de TopBar
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        // Accion del boton crear
        findViewById<LinearLayout>(R.id.bottonCrearCitaMedica).setOnClickListener {
            startActivity(Intent(this, CrearModuloCitas::class.java))
        }

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

        // Inicializa las vistas
        citasRecyclerView = findViewById(R.id.citasRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosView)

        // Configura el RecyclerView
        citasRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosCitas()
    }

    private fun obtenerDatosCitas() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("toma_citas")
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
        val listaCitas = documentos.map { doc ->
            idDocumento = doc.id
            Citas(
                id_usuario = idDocumento,
                especialidad = doc.getString("especialidad") ?: "Sin especialidad",
                fecha = doc.getString("fecha") ?: "Sin fecha",
                hora = doc.getString("hora") ?: "Sin hora",
                nombre_medico = doc.getString("nombre_medico") ?: "Sin nombre médico",
                descripcion = doc.getString("descripcion") ?: "Sin descripcion"
            )
        }

        // Configura el adaptador del RecyclerView
        citasRecyclerView.adapter = CitasAdapter(listaCitas)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        citasRecyclerView.visibility = View.VISIBLE
    }


    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        citasRecyclerView.visibility = View.GONE
    }
}