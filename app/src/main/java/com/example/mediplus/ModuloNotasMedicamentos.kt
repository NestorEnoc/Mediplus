package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.adapter.NotasMedicamento
import com.example.mediplus.adapter.NotasMedicamentoAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ModuloNotasMedicamentos : AppCompatActivity() {

    private lateinit var notasMedicamentosRecyclerView: RecyclerView
    private lateinit var sinDatosView: View
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String
    private lateinit var idDocumento: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo_notas_medicamentos)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        // Accion del boton crear
        findViewById<LinearLayout>(R.id.bottonCrearNotasMedicamentos).setOnClickListener {
            startActivity(Intent(this, CrearModuloNotasMedicamentos::class.java))
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
        notasMedicamentosRecyclerView = findViewById(R.id.notasMedicamentosRecyclerView)
        sinDatosView = findViewById(R.id.sinDatosView)

        // Configura el RecyclerView
        notasMedicamentosRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener los datos de Firestore
        obtenerDatosNotasMedicamentos()
    }

    private fun obtenerDatosNotasMedicamentos() {
        // Creamos la referencia del usuario al documento de usuario específico
        val id_usuario = db.document("/users/$userId")

        db.collection("notas_medicamentos")
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
        val listaNotasMedicamentos = documentos.map { doc ->

            idDocumento = doc.id

            NotasMedicamento(
                id_usuario = idDocumento,
                titulo_nota = doc.getString("titulo") ?: "Sin titulo",
                medicamento = doc.getString("medicamento") ?: "Sin medicamento",
                cuerpo_nota = doc.getString("cuerpo_nota") ?: "Sin nota",
                fecha = doc.getString("fecha") ?: "Sin fecha",
                hora = doc.getString("hora") ?: "Sin nota"
            )
        }

        // Configura el adaptador del RecyclerView
        notasMedicamentosRecyclerView.adapter = NotasMedicamentoAdapter(listaNotasMedicamentos)

        // Muestra el RecyclerView y oculta el mensaje de "sin datos"
        sinDatosView.visibility = View.GONE
        notasMedicamentosRecyclerView.visibility = View.VISIBLE
    }

    private fun mostrarMensajeSinDatos() {
        // Muestra la vista de "sin datos" y oculta el RecyclerView
        sinDatosView.visibility = View.VISIBLE
        notasMedicamentosRecyclerView.visibility = View.GONE
    }
}