package com.example.mediplus

import Medicamento
import MedicamentoAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ModuloMedicamentos : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var medicamentosRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo_medicamentos)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        val bottonCrear = findViewById<LinearLayout>(R.id.bottonCrearMedicamento)

        bottonCrear.setOnClickListener {
            val crear = Intent(this, CrearModuloMedicamentos::class.java)
            startActivity(crear)
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
        medicamentosRecyclerView = findViewById(R.id.medicamentosRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosView)

        // Configura el RecyclerView
        medicamentosRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosMedicamentos()

    }

    /*private fun obtenerDatosMedicamentos() {
        db.collection("toma_medicamentos").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentos = task.result
                if (!documentos.isEmpty) {
                    // Si hay datos, muestra las CardViews
                    mostrarMedicamentos(documentos)
                } else {
                    // Si no hay datos, muestra el mensaje de "sin datos"
                    mostrarMensajeSinDatos()
                }
            } else {
                Log.e("FirestoreError", "Error al obtener datos", task.exception)
                mostrarMensajeSinDatos() // Muestra el mensaje de "sin datos" en caso de error
            }
        }
    }*/



    /*private fun obtenerDatosMedicamentos() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("toma_medicamentos")
            .whereEqualTo("id_usuario", id_usuario)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentos = task.result
                    if (!documentos.isEmpty) {
                        mostrarMedicamentos(documentos)
                    } else {
                        Toast.makeText(this, "No se encontraron documentos", Toast.LENGTH_LONG).show()
                        mostrarMensajeSinDatos()
                    }
                } else {
                    Toast.makeText(this, "Error al obtener datos: ${task.exception}", Toast.LENGTH_LONG).show()
                    mostrarMensajeSinDatos()
                }
            }
    }*/


    private fun obtenerDatosMedicamentos() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("toma_medicamentos")
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
        val listaMedicamentos = documentos.map { doc ->

            idDocumento = doc.id

            Medicamento(
                nombre_medicamento = doc.getString("nombre_medicamento") ?: "Sin nombre",
                fecha_desde = doc.getString("fecha_desde") ?: "Sin fecha",
                num_dosis = doc.getString("num_dosis") ?: "Sin dosis",
                id_usuario = idDocumento,
                descripcion = doc.getString("descripcion")?: "Sin descripcion",
                fecha_hasta = doc.getString("fecha_hasta")?: "Sin fecha hasta",
                hora = doc.getString("hora")?: "Sin hora",
                hora_mas_veces = doc.getString("hora_mas_veces")?: "Sin hora mas veces",
                intervalo_horas = doc.getString("intervalo_horas")?: "Sin intervalo de horas",
                presentacion = doc.getString("presentacion")?: "Sin presentacion",
                unidad = doc.getString("unidad")?: "Sin unidad"
            )
        }

        // Configura el adaptador del RecyclerView
        medicamentosRecyclerView.adapter = MedicamentoAdapter(listaMedicamentos)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        medicamentosRecyclerView.visibility = View.VISIBLE
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        medicamentosRecyclerView.visibility = View.GONE
    }
}