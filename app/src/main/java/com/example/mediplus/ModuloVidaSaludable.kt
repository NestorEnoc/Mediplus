package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.Salud
import com.example.mediplus.adapter.VidaSaludableAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ModuloVidaSaludable : AppCompatActivity() {

    private lateinit var vidaSaludableRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo_vida_saludable)

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
        vidaSaludableRecyclerView = findViewById(R.id.vidaSaludableRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosViewVidaSaludable)

        // Configura el RecyclerView
        vidaSaludableRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosMedicamentos()

        /**/

        val bottonCrear = findViewById<LinearLayout>(R.id.bottonCrearVidaSaludable)

        bottonCrear.setOnClickListener {
            val crear = Intent(this, CrearModuloVidaSaludable::class.java)
            startActivity(crear)
        }
    }

    private fun obtenerDatosMedicamentos() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("vida_saludable")
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
        val listaVidaSaludable = documentos.map { doc ->

            idDocumento = doc.id

            Salud(
                actividad = doc.getString("actividad") ?: "Sin actividad",
                categoria = doc.getString("categoria") ?: "Sin fecha",
                id_usuario = idDocumento,
                descripcion = doc.getString("descripcion")?: "Sin descripcion",
                fecha = doc.getString("fecha")?: "Sin fecha hasta",
                hora = doc.getString("hora")?: "Sin hora"
            )
        }

        // Configura el adaptador del RecyclerView
        vidaSaludableRecyclerView.adapter = VidaSaludableAdapter(listaVidaSaludable)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        vidaSaludableRecyclerView.visibility = View.VISIBLE
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        vidaSaludableRecyclerView.visibility = View.GONE
    }
}