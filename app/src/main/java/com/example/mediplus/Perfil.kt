package com.example.mediplus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Perfil : AppCompatActivity() {

    private var nombres: String = ""
    private var apellidos: String = ""
    private var telefono: String = ""
    private var correo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val buttonRegressMenuPrincipal = findViewById<ImageView>(R.id.buttom_regresar_menu_principal)
        buttonRegressMenuPrincipal.setOnClickListener {
            finish()
        }

        val editPerfil = findViewById<ImageView>(R.id.editar)
        editPerfil.setOnClickListener {
            val intent = Intent(this, Editar_perfil::class.java).apply {
                putExtra("nombres", nombres)
                putExtra("apellidos", apellidos)
                putExtra("telefono", telefono)
                putExtra("correo", correo)
            }
            startActivity(intent)
        }

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            getUserData(userId) // Llamar a getUserData con el userId
        } else {
            Toast.makeText(this, "User ID no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserData(userId: String) {
        val db = FirebaseFirestore.getInstance()

        // Acceder al documento del usuario en la colección "users"
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Obtener datos del documento
                    nombres = document.getString("nombres") ?: ""
                    apellidos = document.getString("apellidos") ?: ""
                    telefono = document.getString("telefono") ?: ""
                    correo = document.getString("correo") ?: ""


                    // Aquí puedes usar los datos obtenidos y asignarlos a los TextViews
                    findViewById<TextView>(R.id.txtNombres).text = nombres
                    findViewById<TextView>(R.id.txtApellidos).text = apellidos
                    findViewById<TextView>(R.id.txtTelefono).text = telefono
                    findViewById<TextView>(R.id.txtCorreo).text = correo

                } else {
                    Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al recuperar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
