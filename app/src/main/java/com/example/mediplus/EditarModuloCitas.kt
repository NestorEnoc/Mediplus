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

class EditarModuloCitas : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var editTextDate: EditText
    private lateinit var btnSelectTime: EditText

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_modulo_citas)

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
        val especialidad = intent.getStringExtra("especialidad")
        val fecha = intent.getStringExtra("fecha")
        val hora = intent.getStringExtra("hora")
        val nombre_medico = intent.getStringExtra("nombre_medico")
        val descripcion = intent.getStringExtra("descripcion")

        // Asignar los datos a los TextViews
        findViewById<TextView>(R.id.txtEspecialidadEditar).text = especialidad
        findViewById<TextView>(R.id.txtFechaCitaEditar).text = fecha
        findViewById<TextView>(R.id.txtHoraCitaEditar).text = hora
        findViewById<TextView>(R.id.txtNombreMedicoCitaEditar).text = nombre_medico
        findViewById<TextView>(R.id.txtDescripcionCitaEditar).text = descripcion

        /**/

        // Accion para el select de bienestar
        val seleccionEspecialidad: MaterialAutoCompleteTextView = findViewById(R.id.txtEspecialidadEditar)

        // Define las opciones del dropdown
        val opcionesEspecialidad = arrayOf("Cardiologia", "Medicina general", "Medicina interna", "Laboratorio", "Fisioterapia")

        // Crea un ArrayAdapter con las opciones
        val adapterArray = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesEspecialidad)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        seleccionEspecialidad.setAdapter(adapterArray)

        /**/

        // Para el selector de fecha
        editTextDate = findViewById(R.id.txtFechaCitaEditar)

        // Configurar el listener para el EditText
        editTextDate.setOnClickListener {
            showDatePickerDialog(editTextDate)
        }

        /**/

        //Para el selector de hora TimePickerDialog
        btnSelectTime = findViewById(R.id.txtHoraCitaEditar)

        btnSelectTime.setOnClickListener {
            showTimePickerDialog(btnSelectTime)
        }

        /**/
        // Accion de registrar una actividad
        findViewById<LinearLayout>(R.id.btnEditarRecordatorioCitaMedica).setOnClickListener {
            if (id_usuario != null) {
                if (validarCampos()) {
                    editarCitaMedica(id_usuario)
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

    // Metodo de editar examen
    private fun editarCitaMedica(examenId: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            val especialidaaActualizado = findViewById<EditText>(R.id.txtEspecialidadEditar).text.toString()
            val fechaActualizada = findViewById<EditText>(R.id.txtFechaCitaEditar).text.toString()
            val horaActualizada = findViewById<EditText>(R.id.txtHoraCitaEditar).text.toString()
            val nombreMedicoActualizada = findViewById<EditText>(R.id.txtNombreMedicoCitaEditar).text.toString()
            val descripcionActualizada = findViewById<EditText>(R.id.txtDescripcionCitaEditar).text.toString()

            if (examenId.isEmpty()) {
                Toast.makeText(this, "ID del medicamento no válido", Toast.LENGTH_SHORT).show()
                return
            }

            val medicamentoActualizado = hashMapOf(
                "especialidad" to especialidaaActualizado,
                "fecha" to fechaActualizada,
                "hora" to horaActualizada,
                "nombre_medico" to nombreMedicoActualizada,
                "descripcion" to descripcionActualizada
            )

            db.collection("toma_citas").document(examenId)
                .set(medicamentoActualizado, SetOptions.merge())
                .addOnSuccessListener {
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
        startActivity(Intent(this, ConfirmacionEdicionCitas::class.java))
        finish()
    }

    @SuppressLint("CutPasteId")
    private fun validarCampos(): Boolean {
        // Obtener los valores de los campos
        val especialidad = findViewById<EditText>(R.id.txtEspecialidadEditar).text.toString().trim()
        val fecha = findViewById<EditText>(R.id.txtFechaCitaEditar).text.toString().trim()
        val hora = findViewById<EditText>(R.id.txtHoraCitaEditar).text.toString().trim()
        val medico = findViewById<EditText>(R.id.txtNombreMedicoCitaEditar).text.toString().trim()
        val descripcion = findViewById<EditText>(R.id.txtDescripcionCitaEditar).text.toString().trim()

        // Referencias a los EditText
        val inputLayoutEspecialidad = findViewById<EditText>(R.id.txtEspecialidadEditar)
        val inputLayoutFecha = findViewById<EditText>(R.id.txtFechaCitaEditar)
        val inputLayoutHora = findViewById<EditText>(R.id.txtHoraCitaEditar)
        val inputLayoutMedico = findViewById<EditText>(R.id.txtNombreMedicoCitaEditar)
        val inputLayoutDescripcion = findViewById<EditText>(R.id.txtDescripcionCitaEditar)

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        var isValid = true

        inputLayoutEspecialidad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutEspecialidad.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutEspecialidad.error = "Por favor, seleccione una especialidad" // Error visible
                } else {
                    inputLayoutEspecialidad.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutEspecialidad.error = null // Eliminar el error
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

        inputLayoutMedico.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutMedico.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutMedico.error = "Por favor, ingrese el nombre del medico"
                } else {
                    inputLayoutMedico.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutMedico.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputLayoutDescripcion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    inputLayoutDescripcion.setBackgroundResource(R.drawable.border_validacion) // Rojo
                    inputLayoutDescripcion.error = "Por favor, ingrese una descripción"
                } else {
                    inputLayoutDescripcion.setBackgroundResource(R.drawable.border) // Azul
                    inputLayoutDescripcion.error = null // Eliminar el error
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Comprobar si alguno de los campos está vacío y cambiar el borde a rojo si es necesario
        if (especialidad.isEmpty()) {
            inputLayoutEspecialidad.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutEspecialidad.error = "Por favor, seleccione una enfermedad"
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

        if (medico.isEmpty()) {
            inputLayoutMedico.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutMedico.error = "Por favor, ingrese el nombre del medico"
            isValid = false
        }

        if (descripcion.isEmpty()) {
            inputLayoutDescripcion.setBackgroundResource(R.drawable.border_validacion)
            inputLayoutDescripcion.error = "Por favor, ingrese una descripción"
            isValid = false
        }

        return isValid
    }
}