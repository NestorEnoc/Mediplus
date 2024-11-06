package com.example.mediplus.inicio_sesion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mediplus.MainActivity
import com.example.mediplus.R
import com.example.mediplus.Registrarse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Color
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val handler = Handler()
    private lateinit var loadingRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val buttonIniciarSesion = findViewById<LinearLayout>(R.id.ButtonIniciarSesionLogin)

        buttonIniciarSesion.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            //loginUser()
            if (validarCampos()) {
                loginUser()
            }else{
                Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_LONG).show()
            }
        }

        val buttonRegister = findViewById<LinearLayout>(R.id.ButtonRegister)

        buttonRegister.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)
            startActivity(intent)
        }

    }

    /*private fun loginUser() {
        val email = findViewById<EditText>(R.id.txtCorreoLogin).text.toString()
        val pass = findViewById<EditText>(R.id.txtPasswordLogin).text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Redirigir a otra actividad
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Opcional: finaliza la actividad actual
                    } else {
                        val errorMessage = task.exception?.message ?: "Error desconocido"
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }*/

    /*private fun loginUser() {
        val email = findViewById<EditText>(R.id.txtCorreoLogin).text.toString()
        val pass = findViewById<EditText>(R.id.txtPasswordLogin).text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(userId!!)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val names = document.getString("nombres") ?: ""
                                    val surnames = document.getString("apellidos") ?: ""

                                    // Obtener la primera letra del nombre y apellido en mayúsculas
                                    val firstLetterName = names.firstOrNull()?.toUpperCase() ?: ' '
                                    val firstLetterSurname = surnames.firstOrNull()?.toUpperCase() ?: ' '

                                    // Combinar las letras en una cadena
                                    val usuarioLogeado = "$firstLetterName$firstLetterSurname"

                                    val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                                    sharedPreferences.edit().putString("usuarioLogeado", usuarioLogeado).apply()

                                    // Redirigir a otra actividad y pasar las iniciales
                                    val intent = Intent(this, MainActivity::class.java).apply {
                                        putExtra("usuarioLogeado", usuarioLogeado) // Pasar las iniciales
                                    }
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al recuperar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        val errorMessage = task.exception?.message ?: "Error desconocido"
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun loginUser() {
        val email = findViewById<EditText>(R.id.txtCorreoLogin).text.toString()
        val pass = findViewById<EditText>(R.id.txtPasswordLogin).text.toString()
        val loadingIndicator = findViewById<LinearLayout>(R.id.loadingIndicator)
        val loadingDots = findViewById<TextView>(R.id.loadingDots)

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            loadingIndicator.visibility = View.VISIBLE // Muestra el loading indicator

            // Iniciar la animación
            animateLoadingDots(loadingDots)

            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        // Guardar userId en SharedPreferences
                        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                        sharedPreferences.edit().putString("userId", userId).apply()

                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(userId!!)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val names = document.getString("nombres") ?: ""
                                    val surnames = document.getString("apellidos") ?: ""

                                    val firstLetterName = names.firstOrNull()?.uppercaseChar() ?: ' '
                                    val firstLetterSurname = surnames.firstOrNull()?.uppercaseChar() ?: ' '

                                    val usuarioLogeado = "$firstLetterName$firstLetterSurname"

                                    val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                                    sharedPreferences.edit().putString("usuarioLogeado", usuarioLogeado).apply()
                                    //iniciar(usuarioLogeado)

                                    // Mostrar mensaje de "Cargando..."
                                    Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this, MainActivity::class.java).apply {
                                        putExtra("usuarioLogeado", usuarioLogeado)
                                    }
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                                }
                                loadingIndicator.visibility = View.GONE // Oculta el loading indicator
                                handler.removeCallbacks(loadingRunnable) // Detiene la animación
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al recuperar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                loadingIndicator.visibility = View.GONE
                                handler.removeCallbacks(loadingRunnable) // Detiene la animación
                            }
                    } else {
                        val errorMessage = when (task.exception?.message) {
                            "The password is invalid or the user does not have a password." -> "Contraseña incorrecta"
                            "There is no user record corresponding to this identifier. The user may have been deleted." -> "Usuario no encontrado"
                            "The email address is badly formatted." -> "El formato del correo electrónico es inválido"
                            "The supplied auth credential is incorrect, malformed or has expired." -> "Las credenciales son incorrectas o han caducado"
                            else -> "Error desconocido: ${task.exception?.message}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        loadingIndicator.visibility = View.GONE // Oculta el loading indicator
                        handler.removeCallbacks(loadingRunnable) // Detiene la animación
                    }
                }
        } else {
            Toast.makeText(this, "Debe completar los campos para iniciar sesion", Toast.LENGTH_SHORT).show()
        }
    }

    private fun animateLoadingDots(loadingDots: TextView) {
        val colors = arrayOf(Color.GRAY, Color.RED, Color.GRAY, Color.GRAY) // Colores de los puntos
        val dotCount = 3
        var currentIndex = 0

        loadingRunnable = object : Runnable {
            override fun run() {
                val coloredDots = StringBuilder()
                for (i in 0 until dotCount) {
                    coloredDots.append("●")
                }
                loadingDots.text = coloredDots.toString()
                loadingDots.setTextColor(colors[currentIndex])

                currentIndex = (currentIndex + 1) % dotCount
                handler.postDelayed(this, 300)
            }
        }

        handler.post(loadingRunnable)
    }

    fun iniciar (usuarioLogeado: String){
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("usuarioLogeado", usuarioLogeado)
        }
        startActivity(intent)
        finish()
    }

    @SuppressLint("CutPasteId")
    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val correo = findViewById<EditText>(R.id.txtCorreoLogin).text.toString().trim()
        val password = findViewById<EditText>(R.id.txtPasswordLogin).text.toString().trim()

        // Referencias a los EditText
        val inputLayoutCorreo = findViewById<EditText>(R.id.txtCorreoLogin)
        val inputLayoutPassword = findViewById<EditText>(R.id.txtPasswordLogin)

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        var isValid = true

        inputLayoutCorreo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutCorreo.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutCorreo.error = "Por favor, ingrese su correo electrónico" // Error visible
                } else {
                    inputLayoutCorreo.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutCorreo.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutPassword.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutPassword.error = "Por favor, ingrese su contraseña"
                } else {
                    inputLayoutPassword.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutPassword.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        if (correo.isEmpty()) {
            inputLayoutCorreo.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutCorreo.error = "Por favor, ingrese su correo electrónico"
            isValid = false
        }

        if (password.isEmpty()) {
            inputLayoutPassword.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutPassword.error = "Por favor, ingrese su contraseña"
            isValid = false
        }

        return isValid
    }
}