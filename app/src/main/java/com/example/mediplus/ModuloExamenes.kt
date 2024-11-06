package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.Examenes
import com.example.mediplus.adapter.ExamenesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ModuloExamenes : AppCompatActivity() {

    private lateinit var examenesRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo_examenes)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        // Crear
        val bottonCrear = findViewById<LinearLayout>(R.id.bottonCrearExamenes)

        bottonCrear.setOnClickListener {
            startActivity(Intent(this, CrearModuloExamenes::class.java))
        }

        // Filtrar
        val bottonFiltrar = findViewById<LinearLayout>(R.id.bottonFiltrar)

        bottonFiltrar.setOnClickListener {
            startActivity(Intent(this, HistorialModuloExamenes::class.java))
        }

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

        // Inicializa las vistas
        examenesRecyclerView = findViewById(R.id.examenesRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosView)

        // Configura el RecyclerView
        examenesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosExamenes()
    }

    private fun obtenerDatosExamenes() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("examenes")
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
        val listaExamenes = documentos.map { doc ->

            idDocumento = doc.id

            Examenes(
                id_usuario = idDocumento,
                nombre_examen = doc.getString("nombre_examen") ?: "Sin nombre examen",
                fecha = doc.getString("fecha") ?: "Sin fecha",
                hora = doc.getString("hora") ?: "Sin hora",
                especialidad = doc.getString("especialidad")?: "Sin especialidad",
                entidad = doc.getString("entidad")?: "Sin entidad",
                nombre_doctor = doc.getString("nombre_doctor")?: "Sin nombre doctor",
                descripcion = doc.getString("descripcion")?: "Sin descripcion"
            )
        }

        // Configura el adaptador del RecyclerView
        examenesRecyclerView.adapter = ExamenesAdapter(listaExamenes)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        examenesRecyclerView.visibility = View.VISIBLE
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        examenesRecyclerView.visibility = View.GONE
    }
}