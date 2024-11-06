package com.example.mediplus

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.HistorialAbastecimiento
import com.example.mediplus.adapter.HistorialAbastecimientoAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HistorialModuloAbastecimiento : AppCompatActivity() {

    private lateinit var abastecimientoHistorialRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String
    private var listaAbastecimientoFiltrados: List<HistorialAbastecimiento> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_modulo_abastecimiento)

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
        abastecimientoHistorialRecyclerView = findViewById(R.id.abastecimientoHistorialRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosViewAbastecimientoHistorial)

        // Configura el RecyclerView
        abastecimientoHistorialRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosHistorialAbastecimiento()

        /**/

        val etFiltroMedicamentos = findViewById<EditText>(R.id.txtFiltroAbastecimientos)
        val btnBuscarMedicamento = findViewById<LinearLayout>(R.id.btnBuscarAbastecimiento)

        btnBuscarMedicamento.setOnClickListener {
            val palabraFiltrada = etFiltroMedicamentos.text.toString()
            filtrarAbastecimiento(palabraFiltrada)
        }
    }

    private fun obtenerDatosHistorialAbastecimiento() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("archivar_abastecimiento")
            .whereEqualTo("id_usuario", id_usuario)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
                    mostrarMensajeSinDatos()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    mostrarAbastecimientoMedicamentos(snapshot)
                } else {
                    //Toast.makeText(this, "No se encontraron documentos", Toast.LENGTH_LONG).show() <!-- Ultimo-->
                    mostrarMensajeSinDatos()
                }
            }
    }


    private fun mostrarAbastecimientoMedicamentos(documentos: QuerySnapshot) {
        val listaHistorialAbastecimiento = documentos.map { doc ->

            idDocumento = doc.id

            HistorialAbastecimiento(
                id_usuario = idDocumento,
                medicamento = doc.getString("medicamento") ?: "Sin nombre",
                fecha_abastecimiento = doc.getString("fecha_abastecimiento") ?: "Sin fecha",
                cantidad = doc.getString("cantidad") ?: "Sin cantidad"
            )
        }

        // Inicializa el RecyclerView con la lista original
        listaAbastecimientoFiltrados = listaHistorialAbastecimiento
        // Configura el adaptador del RecyclerView
        abastecimientoHistorialRecyclerView.adapter = HistorialAbastecimientoAdapter(listaAbastecimientoFiltrados)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        abastecimientoHistorialRecyclerView.visibility = View.VISIBLE
    }

    private fun filtrarAbastecimiento(palabra: String) {
        val listaFiltrada = listaAbastecimientoFiltrados.filter { historial ->
            historial.fecha_abastecimiento.contains(palabra, ignoreCase = true) ||
            historial.medicamento.contains(palabra, ignoreCase = true) ||
            historial.cantidad.contains(palabra, ignoreCase = true)
        }
        // Actualiza el adaptador con la lista filtrada
        abastecimientoHistorialRecyclerView.adapter = HistorialAbastecimientoAdapter(listaFiltrada)
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        abastecimientoHistorialRecyclerView.visibility = View.GONE
    }
}