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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Calendar

class EditarModuloGestionSalud : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var editTextDate: EditText
    private lateinit var btnSelectTime: EditText

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_modulo_gestion_salud)

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

        // Obtener los datos del Intent
        val id_usuario = intent.getStringExtra("id_usuario")
        val enfermedad = intent.getStringExtra("enfermedad")
        val fecha = intent.getStringExtra("fecha")
        val hora = intent.getStringExtra("hora")
        val categoria = intent.getStringExtra("categoria")
        val descripcion = intent.getStringExtra("descripcion")


        // Asignar los datos a los TextViews
        findViewById<TextView>(R.id.txtEnfermedadEditar).text = enfermedad
        findViewById<TextView>(R.id.txtFechaGestionSaludEditar).text = fecha
        findViewById<TextView>(R.id.txtHoraGestionSaludEditar).text = hora
        findViewById<TextView>(R.id.txtCategoriaGestionSaludEditar).text = categoria
        findViewById<TextView>(R.id.txtDescripcionGestionSaludEditar).text = descripcion

        /**/

        // Accion para el select de bienestar
        val seleccionEnfermedad: MaterialAutoCompleteTextView = findViewById(R.id.txtEnfermedadEditar)

        // Define las opciones del dropdown
        val opcionesEnfermedad = arrayOf("Hipertensión Arterial", "Hipotensión Arterial", "Diabetes", "Asma", "Enfermedades del corazón", "Alergias", "Infección", "Otra enfermedad")

        // Crea un ArrayAdapter con las opciones
        val adapterArrayEnfermedad = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesEnfermedad)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        seleccionEnfermedad.setAdapter(adapterArrayEnfermedad)

        /**/

        // Accion para el select de categoria
        val seleccionCategoria: MaterialAutoCompleteTextView = findViewById(R.id.txtCategoriaGestionSaludEditar)

        // Define las opciones del dropdown
        val opcionesCategoria = arrayOf("Enfermedades generales", "Infecciosas", "Genéticas o hereditarias", "Autoinmunes", "Recurrentes", "Progresivas", "Otra categoria")

        // Crea un ArrayAdapter con las opciones
        val adapterArrayCategoria = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesCategoria)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        seleccionCategoria.setAdapter(adapterArrayCategoria)

        /**/

        // Para el selector de fecha
        editTextDate = findViewById(R.id.txtFechaGestionSaludEditar)

        // Configurar el listener para el EditText
        editTextDate.setOnClickListener {
            showDatePickerDialog(editTextDate)
        }

        /**/

        //Para el selector de hora TimePickerDialog
        btnSelectTime = findViewById(R.id.txtHoraGestionSaludEditar)

        btnSelectTime.setOnClickListener {
            showTimePickerDialog(btnSelectTime)
        }

        /**/

        // Accion de registrar una actividad
        findViewById<LinearLayout>(R.id.btnEditarRecordatorioGestionSalud).setOnClickListener {
            // Supongamos que has pasado el medicamentoId a esta actividad
            if (id_usuario != null) {
                if (validarCampos()) {
                    updateGestionSaludData(id_usuario)
                }else{
                    Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Error: ${id_usuario} no encontrado", Toast.LENGTH_LONG).show()
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

    private fun updateGestionSaludData(enfermedadId: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            val enfermedad = findViewById<EditText>(R.id.txtEnfermedadEditar).text.toString()
            val categoria = findViewById<EditText>(R.id.txtCategoriaGestionSaludEditar).text.toString()
            val descripcion = findViewById<EditText>(R.id.txtDescripcionGestionSaludEditar).text.toString()
            val fecha = findViewById<EditText>(R.id.txtFechaGestionSaludEditar).text.toString()
            val hora = findViewById<EditText>(R.id.txtHoraGestionSaludEditar).text.toString()

            if (enfermedadId.isEmpty()) {
                Toast.makeText(this, "ID de la enfermedad no válido", Toast.LENGTH_SHORT).show()
                return
            }

            val gestionSaludActualizada = hashMapOf(
                "enfermedad" to enfermedad,
                "categoria" to categoria,
                "descripcion" to descripcion,
                "fecha" to fecha,
                "hora" to hora
            )

            db.collection("gestion_salud").document(enfermedadId)
                .set(gestionSaludActualizada, SetOptions.merge())
                .addOnSuccessListener {
                    //Toast.makeText(this, "Actividad actualizada correctamente", Toast.LENGTH_SHORT).show()
                    // Redirigir a la actividad de confirmación
                    redirectToConfirmation()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar datos en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        val confirm = Intent(this, ConfirmacionEdicionGestionSalud::class.java)
        startActivity(confirm)
        finish() // Opcional: Cierra la actividad actual si no quieres volver a ella
    }

    // Metodo para validar campos del formulario
    @SuppressLint("CutPasteId")
    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val enfermedad = findViewById<EditText>(R.id.txtEnfermedadEditar).text.toString().trim()
        val fecha = findViewById<EditText>(R.id.txtFechaGestionSaludEditar).text.toString().trim()
        val hora = findViewById<EditText>(R.id.txtHoraGestionSaludEditar).text.toString().trim()
        val categoria = findViewById<EditText>(R.id.txtCategoriaGestionSaludEditar).text.toString().trim()
        val descripcion = findViewById<EditText>(R.id.txtDescripcionGestionSaludEditar).text.toString().trim()

        // Referencias a los EditText
        val inputLayoutEnfermedad = findViewById<EditText>(R.id.txtEnfermedadEditar)
        val inputLayoutFecha = findViewById<EditText>(R.id.txtFechaGestionSaludEditar)
        val inputLayoutHora = findViewById<EditText>(R.id.txtHoraGestionSaludEditar)
        val inputLayoutCategoria = findViewById<EditText>(R.id.txtCategoriaGestionSaludEditar)
        val inputLayoutDescripcion = findViewById<EditText>(R.id.txtDescripcionGestionSaludEditar)

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        var isValid = true

        inputLayoutEnfermedad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutEnfermedad.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutEnfermedad.error = "Por favor, seleccione una enfermedad" // Error visible
                } else {
                    inputLayoutEnfermedad.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutEnfermedad.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutFecha.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutFecha.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutFecha.error = "Por favor, selecciona una fecha"
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
                    inputLayoutHora.error = "Por favor, selecciona una hora"
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
                    inputLayoutCategoria.error = "Por favor, selecciona una categoría"
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
        if (enfermedad.isEmpty()) {
            inputLayoutEnfermedad.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutEnfermedad.error = "Por favor, seleccione una enfermedad"
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