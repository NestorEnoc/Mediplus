package com.example.mediplus

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class CrearModuloVidaSaludable : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var editTextDate: EditText
    private lateinit var btnSelectTime: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_modulo_vida_saludable)

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

        // Accion para el select de bienestar
        val seleccioneActividad: MaterialAutoCompleteTextView = findViewById(R.id.txtActividad)

        // Define las opciones del dropdown
        val opcionesActividad = arrayOf("Ejercicios anti-estres", "Yoga y meditación", "Ejercicios de respiración", "Estiramientos y movilidad", "Cuerpo y Mente", "Gym", "Clases de Zumba o danza Libre", "Otra actividad")

        // Crea un ArrayAdapter con las opciones
        val adapterArrayActividad = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesActividad)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        seleccioneActividad.setAdapter(adapterArrayActividad)

        /**/

        // Accion para el select de categoria
        val seleccionCategoria: MaterialAutoCompleteTextView = findViewById(R.id.txtCategoria)

        // Define las opciones del dropdown
        val opcionesCategoria = arrayOf("Relax", "Creativas y Artísticas", "Tiempo de desconexión", "Ejercicio", "Técnicas de Relajación", "Otra categoria")

        // Crea un ArrayAdapter con las opciones
        val adapterArrayCategoria = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesCategoria)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        seleccionCategoria.setAdapter(adapterArrayCategoria)

        /**/

        // Para el selector de fecha
        editTextDate = findViewById(R.id.txtFecha)

        // Configurar el listener para el EditText
        editTextDate.setOnClickListener {
            showDatePickerDialog(editTextDate)
        }

        /**/

        //Para el selector de hora TimePickerDialog
        btnSelectTime = findViewById(R.id.txtHora)

        btnSelectTime.setOnClickListener {
            showTimePickerDialog(btnSelectTime)
        }

        /**/

        // Accion de registrar una actividad
        findViewById<LinearLayout>(R.id.btnCrearRecordatorioVidaSaludable).setOnClickListener {
            if (validarCampos()) {
                registrarActividad()
            }else{
                Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Metodo para llamar el selector de fechas
    private fun showDatePickerDialog(editText: EditText) {
        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear el DatePickerDialog
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Mostrar la fecha seleccionada en el EditText correspondiente
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            editText.setText(formattedDate)
        }, year, month, day)

        // Mostrar el dialogo
        datePickerDialog.show()
    }

    // Metodo para llamar el selector de hora
    private fun showTimePickerDialog(editText: EditText) {
        // Obtener la hora actual
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Crear el TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, //R.style.CustomTimePickerDialog,
            { _, selectedHour, selectedMinute ->
                // Convertir la hora a formato de 12 horas
                val amPm = if (selectedHour >= 12) "pm" else "am"
                val hourIn12Format = if (selectedHour % 12 == 0) 12 else selectedHour % 12

                // Mostrar la hora seleccionada con AM o PM
                editText.setText(String.format("%02d:%02d %s", hourIn12Format, selectedMinute, amPm))
            }, hour, minute, false) // false para formato de 12 horas

        // Mostrar el diálogo
        try {
            timePickerDialog.show()
        } catch (e: Exception) {
            e.printStackTrace() // Capturar cualquier excepción
        }
    }

    // Metodo para registrar una actividad
    private fun registrarActividad() {
        val db = FirebaseFirestore.getInstance()

        // Obtener los nuevos datos de los EditText (cambia los IDs según tu layout)
        val actividad = findViewById<EditText>(R.id.txtActividad).text.toString()
        val fecha = findViewById<EditText>(R.id.txtFecha).text.toString()
        val hora = findViewById<EditText>(R.id.txtHora).text.toString()
        val categoria = findViewById<EditText>(R.id.txtCategoria).text.toString()
        val descripcion = findViewById<EditText>(R.id.txtDescripcionActividad).text.toString()

        // Crear una referencia al documento de usuario
        val usuarioRef = db.document("/users/$userId")

        // Crear un mapa con los datos a registrar en Firestore
        val medicamentoData = hashMapOf(
            "id_usuario" to usuarioRef,  // Usamos la referencia en lugar de solo el userId
            "actividad" to actividad,
            "fecha" to fecha,
            "hora" to hora,
            "categoria" to categoria,
            "descripcion" to descripcion,
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("vida_saludable")
            .add(medicamentoData)  // Usa .add para generar un ID único automáticamente
            .addOnSuccessListener { documentReference ->
                // Mensaje de éxito
                //Toast.makeText(this, "Actividad registrada con éxito: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                // Redirigir a la actividad de confirmación
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar medicamento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        val confirmar = Intent(this, ConfirmacionRecordatorioVidaSaludable::class.java)
        startActivity(confirmar)
        finish() // Opcional: Cierra la actividad actual si no quieres volver a ella
    }

    @SuppressLint("CutPasteId")
    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val actividad = findViewById<EditText>(R.id.txtActividad).text.toString().trim()
        val fecha = findViewById<EditText>(R.id.txtFecha).text.toString().trim()
        val hora = findViewById<EditText>(R.id.txtHora).text.toString().trim()
        val categoria = findViewById<EditText>(R.id.txtCategoria).text.toString().trim()
        val descripcion = findViewById<EditText>(R.id.txtDescripcionActividad).text.toString().trim()

        // Referencias a los EditText
        val inputLayoutActividad = findViewById<EditText>(R.id.txtActividad)
        val inputLayoutFecha = findViewById<EditText>(R.id.txtFecha)
        val inputLayoutHora = findViewById<EditText>(R.id.txtHora)
        val inputLayoutCategoria = findViewById<EditText>(R.id.txtCategoria)
        val inputLayoutDescripcion = findViewById<EditText>(R.id.txtDescripcionActividad)

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        var isValid = true

        inputLayoutActividad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutActividad.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutActividad.error = "Por favor, seleccione una actividad" // Error visible
                } else {
                    inputLayoutActividad.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutActividad.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutFecha.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutFecha.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutFecha.error = "Por favor, seleccione una fecha"
                } else {
                    inputLayoutFecha.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutFecha.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutHora.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutHora.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutHora.error = "Por favor, seleccione una hora"
                } else {
                    inputLayoutHora.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutHora.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutCategoria.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutCategoria.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutCategoria.error = "Por favor, seleccione una categoría"
                } else {
                    inputLayoutCategoria.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutCategoria.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutDescripcion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutDescripcion.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutDescripcion.error = "Por favor, ingresa una descripción"
                } else {
                    inputLayoutDescripcion.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutDescripcion.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        if (actividad.isEmpty()) {
            inputLayoutActividad.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutActividad.error = "Por favor, seleccione una enfermedad"
            isValid = false
        }

        if (fecha.isEmpty()) {
            inputLayoutFecha.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutFecha.error = "Por favor, selecciona una fecha"
            isValid = false
        }

        if (hora.isEmpty()) {
            inputLayoutHora.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutHora.error = "Por favor, selecciona una hora"
            isValid = false
        }

        if (categoria.isEmpty()) {
            inputLayoutCategoria.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutCategoria.error = "Por favor, selecciona una categoría"
            isValid = false
        }

        if (descripcion.isEmpty()) {
            inputLayoutDescripcion.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutDescripcion.error = "Por favor, ingresa una descripción"
            isValid = false
        }

        return isValid
    }
}