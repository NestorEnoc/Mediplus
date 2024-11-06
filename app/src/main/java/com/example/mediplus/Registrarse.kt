package com.example.mediplus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registrarse : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        val ImageButton = findViewById<ImageButton>(R.id.activity_login)

        ImageButton.setOnClickListener {
            finish()
        }

        val buttonRegister = findViewById<LinearLayout>(R.id.ButtonIniciarSesion)

        buttonRegister.setOnClickListener {
            finish()
        }


        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        val crearCuenta = findViewById<LinearLayout>(R.id.buttonCrearCuenta)

        crearCuenta.setOnClickListener {
            //val crear_cuenta = Intent(this, ConfirmacionUsuarioCreado::class.java)
            //startActivity(crear_cuenta)

            if (validarCampos()) {
                registerUser()
            }else{
                Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_LONG).show()
            }
        }

    }

    /*private fun registerUser() {
        val nombres = findViewById<EditText>(R.id.txtNombres).text.toString()
        val apellidos = findViewById<EditText>(R.id.txtApellidos).text.toString()
        val telefono = findViewById<EditText>(R.id.txtTelefono).text.toString()
        val correo = findViewById<EditText>(R.id.txtCorreo).text.toString()
        val contraseña = findViewById<EditText>(R.id.txtPassword).text.toString()

        if (correo.isNotEmpty() && contraseña.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        val user = User(nombres, apellidos, telefono, correo)

                        uid?.let {
                            database.reference.child("users").child(uid).setValue(user)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        val crear_cuenta = Intent(this, ConfirmacionUsuarioCreado::class.java)
                                        startActivity(crear_cuenta)
                                    } else {
                                        Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }*/


    /*private fun registerUser() {
        val nombres = findViewById<EditText>(R.id.txtNombres).text.toString()
        val apellidos = findViewById<EditText>(R.id.txtApellidos).text.toString()
        val telefono = findViewById<EditText>(R.id.txtTelefono).text.toString()
        val correo = findViewById<EditText>(R.id.txtCorreo).text.toString()
        val contraseña = findViewById<EditText>(R.id.txtPassword).text.toString()

        if (correo.isNotEmpty() && contraseña.isNotEmpty()) {
            val registerButton = findViewById<LinearLayout>(R.id.buttonCrearCuenta)
            registerButton.isEnabled = false // Deshabilitar el botón

            // Crear un ProgressDialog
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Cargando...")
            progressDialog.setCancelable(false) // Evitar que se cierre al tocar fuera
            progressDialog.show() // Mostrar el ProgressDialog

            auth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener { task ->
                    progressDialog.dismiss() // Ocultar el ProgressDialog
                    registerButton.isEnabled = true // Habilitar el botón

                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        val user = hashMapOf(
                            "nombres" to nombres,
                            "apellidos" to apellidos,
                            "telefono" to telefono,
                            "correo" to correo
                        )

                        // Instancia de Firestore
                        val db = FirebaseFirestore.getInstance()

                        uid?.let {
                            // Guardar el usuario en la colección "users" en Firestore
                            db.collection("users").document(uid)
                                .set(user)
                                .addOnSuccessListener {
                                    // Redirigir a la actividad de confirmación
                                    val crear_cuenta = Intent(this, ConfirmacionUsuarioCreado::class.java)
                                    startActivity(crear_cuenta)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al guardar en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun registerUser() {
        val db = FirebaseFirestore.getInstance()

        // Obtener los datos de los EditText
        val nombres = findViewById<EditText>(R.id.txtNombres).text.toString()
        val apellidos = findViewById<EditText>(R.id.txtApellidos).text.toString()
        val telefono = findViewById<EditText>(R.id.txtTelefono).text.toString()
        val correo = findViewById<EditText>(R.id.txtCorreo).text.toString()
        val contraseña = findViewById<EditText>(R.id.txtPassword).text.toString()

        // Validar que los campos no estén vacíos
        if (correo.isNotEmpty() && contraseña.isNotEmpty() && nombres.isNotEmpty() && apellidos.isNotEmpty()) {
            val registerButton = findViewById<LinearLayout>(R.id.buttonCrearCuenta)
            registerButton.isEnabled = false // Deshabilitar el botón

            // Registrar el usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener { task ->
                    registerButton.isEnabled = true // Habilitar el botón

                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        val user = hashMapOf(
                            "nombres" to nombres,
                            "apellidos" to apellidos,
                            "telefono" to telefono,
                            "correo" to correo
                        )

                        uid?.let {
                            // Guardar el usuario en la colección "users" en Firestore
                            db.collection("users").document(uid)
                                .set(user)
                                .addOnSuccessListener {
                                    // Mensaje de éxito
                                    //Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()

                                    // Mostrar mensaje de "Cargando..."
                                    Toast.makeText(this, "Registrando...", Toast.LENGTH_SHORT).show()
                                    redirectToConfirmation()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al guardar en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        val confirm = Intent(this, ConfirmacionUsuarioCreado::class.java)
        startActivity(confirm)
        finish() // Opcional: Cierra la actividad actual si no quieres volver a ella
    }

    @SuppressLint("CutPasteId")
    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val nombres = findViewById<EditText>(R.id.txtNombres).text.toString().trim()
        val apellidos = findViewById<EditText>(R.id.txtApellidos).text.toString().trim()
        val telefono = findViewById<EditText>(R.id.txtTelefono).text.toString().trim()
        val correo = findViewById<EditText>(R.id.txtCorreo).text.toString().trim()
        val contraseña = findViewById<EditText>(R.id.txtPassword).text.toString().trim()

        // Referencias a los EditText
        val inputLayoutNombres = findViewById<EditText>(R.id.txtNombres)
        val inputLayoutApellidos = findViewById<EditText>(R.id.txtApellidos)
        val inputLayoutTelefono = findViewById<EditText>(R.id.txtTelefono)
        val inputLayoutCorreo = findViewById<EditText>(R.id.txtCorreo)
        val inputLayoutPassword = findViewById<EditText>(R.id.txtPassword)

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        var isValid = true

        inputLayoutNombres.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutNombres.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutNombres.error = "Por favor, ingrese los nombres" // Error visible
                } else {
                    inputLayoutNombres.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutNombres.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutApellidos.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutApellidos.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutApellidos.error = "Por favor, ingrese los apellidos"
                } else {
                    inputLayoutApellidos.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutApellidos.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutTelefono.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutTelefono.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutTelefono.error = "Por favor, singrese un número de telefono"
                } else {
                    inputLayoutTelefono.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutTelefono.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutCorreo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutCorreo.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutCorreo.error = "Por favor, ingrese un correo"
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
                    inputLayoutPassword.error = "Por favor, ingrese una contraseña"
                } else {
                    inputLayoutPassword.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutPassword.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        if (nombres.isEmpty()) {
            inputLayoutNombres.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutNombres.error = "Por favor, ingrese los nombres"
            isValid = false
        }

        if (apellidos.isEmpty()) {
            inputLayoutApellidos.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutApellidos.error = "Por favor, ingrese los apellidos"
            isValid = false
        }

        if (telefono.isEmpty()) {
            inputLayoutTelefono.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutTelefono.error = "Por favor, ingrese un número de telefono"
            isValid = false
        }

        if (correo.isEmpty()) {
            inputLayoutCorreo.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutCorreo.error = "Por favor, ingrese un correo"
            isValid = false
        }

        if (contraseña.isEmpty()) {
            inputLayoutPassword.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutPassword.error = "Por favor, ingresa una contraseña"
            isValid = false
        }

        return isValid
    }
}