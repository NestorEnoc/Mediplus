package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.AbastecimientoMedicamento
import com.example.mediplus.adapter.AbastecimientoMedicamentoAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ModuloAlertaAbastecimiento : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var abastecimientoRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var idDocumento: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo_alerta_abastecimiento)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        /* Codigo para traer las iniciales del usuario logueado y topbarReturn*/
        val usuarioLogeado = sharedPreferences.getString("usuarioLogeado", "")

        // Crear una nueva instancia del fragmento
        val fragment = BarTopReturn().apply { // aca tambien
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

        findViewById<LinearLayout>(R.id.bottonCrearAbastecimiento).setOnClickListener {
            startActivity(Intent(this, CrearModuloAbastecimiento::class.java))
        }

        // Inicializa las vistas
        abastecimientoRecyclerView = findViewById(R.id.abastecimientoRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosView)

        // Configura el RecyclerView
        abastecimientoRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosMedicamentosAlerta()
    }

    private fun obtenerDatosMedicamentosAlerta() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("abastecimiento_medicamentos")
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

            AbastecimientoMedicamento(
                id_usuario = idDocumento,
                nombre_medicamento = doc.getString("medicamento") ?: "Sin nombre",
                fecha_abastecimiento = doc.getString("fecha_abastecimiento") ?: "Sin fecha",
                cantidad = doc.getString("cantidad") ?: "Sin cantidad"
            )
        }

        // Configura el adaptador del RecyclerView
        abastecimientoRecyclerView.adapter = AbastecimientoMedicamentoAdapter(listaMedicamentos)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        abastecimientoRecyclerView.visibility = View.VISIBLE
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        abastecimientoRecyclerView.visibility = View.GONE
    }
}