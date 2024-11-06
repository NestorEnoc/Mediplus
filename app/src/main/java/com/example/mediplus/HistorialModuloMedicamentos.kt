package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.HistorialMedicamento
import com.example.mediplus.adapter.HistorialMedicamentoAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HistorialModuloMedicamentos : AppCompatActivity() {

    private lateinit var medicamentosHistorialRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String
    private var listaMedicamentosFiltrados: List<HistorialMedicamento> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_modulo_medicamentos)

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
        medicamentosHistorialRecyclerView = findViewById(R.id.medicamentosHistorialRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosViewMedicamentosHistorial)

        // Configura el RecyclerView
        medicamentosHistorialRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosHistorialMedicamento()

        /**/

        val etFiltroMedicamentos = findViewById<EditText>(R.id.txtFiltroMedicamentos)
        val btnBuscarMedicamento = findViewById<LinearLayout>(R.id.btnBuscarMedicamentos)

        btnBuscarMedicamento.setOnClickListener {
            val palabraFiltrada = etFiltroMedicamentos.text.toString()
            filtrarMedicamentos(palabraFiltrada) // Llama al método de filtrado
        }

    }

    private fun obtenerDatosHistorialMedicamento() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("archivar_medicamentos")
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
        val listaHistorialMedicamento = documentos.map { doc ->

            idDocumento = doc.id

            HistorialMedicamento(
                nombre_medicamento = doc.getString("nombre_medicamento") ?: "Sin nombre",
                num_dosis = doc.getString("num_dosis") ?: "Sin fecha",
                id_usuario = idDocumento,
                fecha = doc.getString("fecha_desde")?: "Sin fecha hasta",
                hora = doc.getString("hora")?: "Sin hora"
            )
        }

        // Inicializa el RecyclerView con la lista original
        listaMedicamentosFiltrados = listaHistorialMedicamento
        // Configura el adaptador del RecyclerView
        medicamentosHistorialRecyclerView.adapter = HistorialMedicamentoAdapter(listaMedicamentosFiltrados)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        medicamentosHistorialRecyclerView.visibility = View.VISIBLE
    }

    private fun filtrarMedicamentos(palabra: String) {
        val listaFiltrada = listaMedicamentosFiltrados.filter { historial ->
            historial.nombre_medicamento.contains(palabra, ignoreCase = true) ||
            historial.num_dosis.contains(palabra, ignoreCase = true) ||
            historial.fecha.contains(palabra, ignoreCase = true) ||
            historial.hora.contains(palabra, ignoreCase = true)
        }
        // Actualiza el adaptador con la lista filtrada
        medicamentosHistorialRecyclerView.adapter = HistorialMedicamentoAdapter(listaFiltrada)
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        medicamentosHistorialRecyclerView.visibility = View.GONE
    }
}