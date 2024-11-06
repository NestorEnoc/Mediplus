package com.example.mediplus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Editar_perfil : AppCompatActivity() {

    private lateinit var userId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        val buttonRegressMenuPrincipal = findViewById<LinearLayout>(R.id.buttom_regresar_perfil)
        buttonRegressMenuPrincipal.setOnClickListener {
            finish()
        }

        // Recuperar los datos del Intent
        val nombres = intent.getStringExtra("nombres")
        val apellidos = intent.getStringExtra("apellidos")
        val telefono = intent.getStringExtra("telefono")
        val correo = intent.getStringExtra("correo")

        // Asignar los datos a los EditText o TextView en el layout de editar perfil
        findViewById<EditText>(R.id.editTxtNombres).setText(nombres)
        findViewById<EditText>(R.id.editTxtApellidos).setText(apellidos)
        findViewById<EditText>(R.id.editTxtTelefono).setText(telefono)
        findViewById<EditText>(R.id.editTxtCorreo).setText(correo)

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        // Configurar el botón de guardar
        val btnGuardar = findViewById<LinearLayout>(R.id.guardarPerfil)
        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                // Mostrar mensaje de "Cargando..."
                Toast.makeText(this, "Actualizando perfil...", Toast.LENGTH_SHORT).show()
                updateUserData()
            }else{
                Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUserData() {
        val db = FirebaseFirestore.getInstance()

        // Obtener los nuevos datos de los EditText
        val nombresActualizados = findViewById<EditText>(R.id.editTxtNombres).text.toString()
        val apellidosActualizados = findViewById<EditText>(R.id.editTxtApellidos).text.toString()
        val telefonoActualizado = findViewById<EditText>(R.id.editTxtTelefono).text.toString()
        val correoActualizado = findViewById<EditText>(R.id.editTxtCorreo).text.toString()
        val passwordActualizado = findViewById<EditText>(R.id.editTxtPassword).text.toString()

        // Asegúrate de que userId no sea nulo o vacío
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User ID no válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear un mapa con los datos a actualizar en Firestore
        val usuarioActualizado = hashMapOf(
            "nombres" to nombresActualizados,
            "apellidos" to apellidosActualizados,
            "telefono" to telefonoActualizado,
            "correo" to correoActualizado
        )

        // Actualizar el documento del usuario en Firestore
        db.collection("users").document(userId)
            .set(usuarioActualizado, SetOptions.merge())
            .addOnSuccessListener {
                // Actualizar el correo en Firebase Authentication
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    // Actualizar el correo
                    user.updateEmail(correoActualizado)
                        .addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                // Si se proporciona una nueva contraseña, actualizarla
                                if (passwordActualizado.isNotEmpty()) {
                                    user.updatePassword(passwordActualizado)
                                        .addOnCompleteListener { passwordTask ->
                                            if (passwordTask.isSuccessful) {
                                                //Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                                //finish() // Cerrar la actividad después de la actualización

                                                // Redirigir a la actividad de confirmación
                                                val confirmar = Intent(this, ConfirmacionUsuarioActualizado::class.java)
                                                startActivity(confirmar)
                                                finish()
                                            } else {
                                                Toast.makeText(this, "Error al actualizar la contraseña: ${passwordTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    //Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                    //finish() // Cerrar la actividad si no se actualiza la contraseña
                                    // Redirigir a la actividad de confirmación
                                    val confirmar = Intent(this, ConfirmacionUsuarioActualizado::class.java)
                                    startActivity(confirmar)
                                    finish()
                                }
                            } else {
                                Toast.makeText(this, "Error al actualizar el correo: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar datos en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("CutPasteId")
    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val nombres = findViewById<EditText>(R.id.editTxtNombres).text.toString().trim()
        val apellidos = findViewById<EditText>(R.id.editTxtApellidos).text.toString().trim()
        val telefono = findViewById<EditText>(R.id.editTxtTelefono).text.toString().trim()
        //val correo = findViewById<EditText>(R.id.editTxtCorreo).text.toString().trim()
        val contraseña = findViewById<EditText>(R.id.editTxtPassword).text.toString().trim()

        // Referencias a los EditText
        val inputLayoutNombres = findViewById<EditText>(R.id.editTxtNombres)
        val inputLayoutApellidos = findViewById<EditText>(R.id.editTxtApellidos)
        val inputLayoutTelefono = findViewById<EditText>(R.id.editTxtTelefono)
        //val inputLayoutCorreo = findViewById<EditText>(R.id.editTxtCorreo)
        val inputLayoutPassword = findViewById<EditText>(R.id.editTxtPassword)

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

        /*inputLayoutCorreo.addTextChangedListener(object : TextWatcher {
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
        })*/

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

        /*if (correo.isEmpty()) {
            inputLayoutCorreo.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutCorreo.error = "Por favor, ingrese un correo"
            isValid = false
        }*/

        if (contraseña.isEmpty()) {
            inputLayoutPassword.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutPassword.error = "Por favor, ingresa una contraseña"
            isValid = false
        }

        return isValid
    }
}