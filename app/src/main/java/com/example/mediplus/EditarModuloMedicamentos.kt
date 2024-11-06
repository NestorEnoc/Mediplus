package com.example.mediplus

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Calendar

class EditarModuloMedicamentos : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextHasta: EditText
    private lateinit var btnSelectTime: CardView
    private lateinit var txtSelectedTime: TextView
    private lateinit var masVecesAlDia: CardView
    private lateinit var masVecesAlDiatxtTime: TextView

    @SuppressLint("CutPasteId", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_modulo_medicamentos)

        /* Codigo para traer las iniciales del usuario logueado*/
        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
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
        val nombre_medicamento = intent.getStringExtra("nombre_medicamento")
        val fecha_desde = intent.getStringExtra("fecha_desde")
        val num_dosis = intent.getStringExtra("num_dosis")
        val descripcion = intent.getStringExtra("descripcion")
        val fecha_hasta = intent.getStringExtra("fecha_hasta")
        val hora = intent.getStringExtra("hora")
        val hora_mas_veces = intent.getStringExtra("hora_mas_veces")
        val intervalo_horas = intent.getStringExtra("intervalo_horas")
        val presentacion = intent.getStringExtra("presentacion")
        val unidad = intent.getStringExtra("unidad")

        // Asignar los datos a los TextViews
        findViewById<TextView>(R.id.txtNombreMedicamentoEditar).text = nombre_medicamento
        findViewById<TextView>(R.id.fechaDesdeEditar).text = fecha_desde
        findViewById<TextView>(R.id.txtDosisEditar).text = num_dosis
        findViewById<TextView>(R.id.txtDescripcionEditar).text = descripcion
        findViewById<TextView>(R.id.fechaHastaEditar).text = fecha_hasta
        findViewById<TextView>(R.id.txt_selected_time_editar).text = hora
        findViewById<TextView>(R.id.txtMostarHoraMasVecesEditar).text = hora_mas_veces
        findViewById<TextView>(R.id.txtIntervaloHorasEditar).text = intervalo_horas
        findViewById<TextView>(R.id.seleccionMedicamentoEditar).text = presentacion
        findViewById<TextView>(R.id.seleccionUnidadEditar).text = unidad


        // Para el select de presentación del medicamento
        val materialAutoCompleteTextView: MaterialAutoCompleteTextView = findViewById(R.id.seleccionMedicamentoEditar)

        // Define las opciones del dropdown
        val opciones = arrayOf("Pastillas", "Tabletas", "Jarabe", "Granulados", "Ampollas", "Cremas o pomadas", "Inhaladores", "Otra presentación")

        // Crea un ArrayAdapter con las opciones
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opciones)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        materialAutoCompleteTextView.setAdapter(adapter)


        // Para el select de unidad
        val seleccioneUnidad: MaterialAutoCompleteTextView = findViewById(R.id.seleccionUnidadEditar)

        // Define las opciones del dropdown
        val opcionesUnidad = arrayOf("50-100 mg", "500 mg", "200-400 mg", "400-1000 UI", "5-20 mg", "Otra medida")

        // Crea un ArrayAdapter con las opciones
        val adapterArray = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesUnidad)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        seleccioneUnidad.setAdapter(adapterArray)


        // Para el selector de fecha
        editTextDate = findViewById(R.id.fechaDesdeEditar)

        // Configurar el listener para el EditText
        editTextDate.setOnClickListener {
            showDatePickerDialog(editTextDate)
        }

        editTextHasta = findViewById(R.id.fechaHastaEditar)
        editTextHasta.setOnClickListener {
            showDatePickerDialog(editTextHasta)
        }

        val colorGray = ContextCompat.getColor(this, R.color.card)
        val colorWhite = ContextCompat.getColor(this, android.R.color.white)

        // Primera tarjeta
        val cardView1 = findViewById<CardView>(R.id.myCardView)
        val expandableLayout1 = findViewById<LinearLayout>(R.id.expandableLayout)
        val iconToggle1 = findViewById<ImageView>(R.id.iconToggle)
        setupExpandableCard(cardView1, expandableLayout1, iconToggle1, colorGray, colorWhite)

        // Segunda tarjeta
        val cardView2 = findViewById<CardView>(R.id.myCardView2)
        val expandableLayout2 = findViewById<LinearLayout>(R.id.expandableLayout2)
        val iconToggle2 = findViewById<ImageView>(R.id.iconToggle2)
        setupExpandableCard(cardView2, expandableLayout2, iconToggle2, colorGray, colorWhite)

        //TimePickerDialog
        btnSelectTime = findViewById(R.id.btn_select_time_editar)
        txtSelectedTime = findViewById(R.id.txt_selected_time_editar)

        btnSelectTime.setOnClickListener {
            showTimePickerDialog(btnSelectTime, txtSelectedTime)
        }

        masVecesAlDia = findViewById(R.id.btnMasVecesAlDiaEditar)
        masVecesAlDiatxtTime = findViewById(R.id.txtMostarHoraMasVecesEditar)

        masVecesAlDia.setOnClickListener {
            showTimePickerDialog(masVecesAlDia, masVecesAlDiatxtTime)
        }


        // Accion del boton Guardar Recordatorio
        findViewById<LinearLayout>(R.id.btnEditarRecordatorio).setOnClickListener {
            if (id_usuario != null) {
                updateMedicamentoData(id_usuario)
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

    // lógica de expansión, colapso y cambio de ícono
    fun setupExpandableCard(cardView: CardView, expandableLayout: LinearLayout, iconToggle: ImageView, colorGray: Int, colorWhite: Int) {
        // Establecer el color inicial gris
        cardView.setCardBackgroundColor(resources.getColor(R.color.cardView))

        // Manejar el clic en el CardView
        cardView.setOnClickListener {
            if (expandableLayout.visibility == View.GONE) {
                // Expandir: Cambiar color y el ícono a "expandido"
                cardView.setCardBackgroundColor(colorWhite)
                iconToggle.setImageResource(R.drawable.icono_dropdown_contraer) // Cambiar a ícono expandido
            } else {
                // Colapsar: Cambiar color y el ícono a "colapsado"
                cardView.setCardBackgroundColor(resources.getColor(R.color.cardView))
                iconToggle.setImageResource(R.drawable.icono_dropdown) // Cambiar a ícono colapsado
            }

            // Llamar a toggleVisibility para manejar la visibilidad con animación
            toggleVisibility(expandableLayout)
        }
    }


    // Para expandir las ViewCard
    fun toggleVisibility(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
            view.animate().alpha(1.0f).setDuration(300).start()
        } else {
            view.animate().alpha(0.0f).setDuration(300).withEndAction {
                view.visibility = View.GONE
            }.start()
        }
    }

    private fun showTimePickerDialog(cardView: CardView, textView: TextView) {
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
                textView.text = String.format("%02d:%02d %s", hourIn12Format, selectedMinute, amPm)
            }, hour, minute, false) // false para formato de 12 horas

        // Mostrar el diálogo
        try {
            timePickerDialog.show()
        } catch (e: Exception) {
            e.printStackTrace() // Capturar cualquier excepción
        }
    }

    /*private fun updateMedicamentoData(medicamentoId: String) {
        val db = FirebaseFirestore.getInstance()

        // Obtener los nuevos datos de los EditText
        val nombre_actualizado = findViewById<EditText>(R.id.txtNombreMedicamentoEditar).text.toString()
        val fecha_desde_actualizada = findViewById<EditText>(R.id.fechaDesdeEditar).text.toString()
        val num_dosis_actualizada = findViewById<EditText>(R.id.txtDosisEditar).text.toString()
        val descripcion_actualizada = findViewById<EditText>(R.id.txtDescripcionEditar).text.toString()
        val fecha_hasta_actualizada = findViewById<EditText>(R.id.fechaHastaEditar).text.toString()
        val hora_actualizada = findViewById<EditText>(R.id.txt_selected_time_editar).text.toString()
        val hora_mas_veces_actualizada = findViewById<EditText>(R.id.txtMostarHoraMasVecesEditar).text.toString()
        val intervalo_horas_actualizada = findViewById<EditText>(R.id.txtIntervaloHorasEditar).text.toString()
        val presentacion_actualizada = findViewById<EditText>(R.id.seleccionMedicamentoEditar).text.toString()
        val unidad_actualizada = findViewById<EditText>(R.id.seleccionUnidadEditar).text.toString()

        // Asegúrate de que medicamentoId no sea nulo o vacío
        if (medicamentoId.isEmpty()) {
            Toast.makeText(this, "ID del medicamento no válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear un mapa con los datos a actualizar en Firestore
        val medicamentoActualizado = hashMapOf(
            "nombre_medicamento" to nombre_actualizado,
            "fecha_desde" to fecha_desde_actualizada,
            "num_dosis" to num_dosis_actualizada,
            "descripcion" to descripcion_actualizada,
            "fecha_hasta" to fecha_hasta_actualizada,
            "hora" to hora_actualizada,
            "hora_mas_veces" to hora_mas_veces_actualizada,
            "intervalo_horas" to intervalo_horas_actualizada,
            "presentacion" to presentacion_actualizada,
            "unidad" to unidad_actualizada
        )

        // Actualizar el documento del medicamento en Firestore
        db.collection("toma_medicamentos").document(medicamentoId)
            .set(medicamentoActualizado, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Medicamento actualizado correctamente", Toast.LENGTH_SHORT).show()
                // Puedes redirigir a otra actividad o cerrar la actividad actual
                finish() // Cierra la actividad después de la actualización
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar datos en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }*/

    private fun updateMedicamentoData(medicamentoId: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            val nombreActualizado = findViewById<EditText>(R.id.txtNombreMedicamentoEditar).text.toString()
            val fechaDesdeActualizada = findViewById<EditText>(R.id.fechaDesdeEditar).text.toString()
            val numDosisActualizada = findViewById<EditText>(R.id.txtDosisEditar).text.toString()
            val descripcionActualizada = findViewById<EditText>(R.id.txtDescripcionEditar).text.toString()
            val fechaHastaActualizada = findViewById<EditText>(R.id.fechaHastaEditar).text.toString()
            val horaActualizada = findViewById<TextView>(R.id.txt_selected_time_editar).text.toString()
            val horaMasVecesActualizada = findViewById<TextView>(R.id.txtMostarHoraMasVecesEditar).text.toString()
            val intervaloHorasActualizada = findViewById<EditText>(R.id.txtIntervaloHorasEditar).text.toString()
            val presentacionActualizada = findViewById<EditText>(R.id.seleccionMedicamentoEditar).text.toString()
            val unidadActualizada = findViewById<EditText>(R.id.seleccionUnidadEditar).text.toString()

            if (medicamentoId.isEmpty()) {
                Toast.makeText(this, "ID del medicamento no válido", Toast.LENGTH_SHORT).show()
                return
            }

            val medicamentoActualizado = hashMapOf(
                "nombre_medicamento" to nombreActualizado,
                "fecha_desde" to fechaDesdeActualizada,
                "num_dosis" to numDosisActualizada,
                "descripcion" to descripcionActualizada,
                "fecha_hasta" to fechaHastaActualizada,
                "hora" to horaActualizada,
                "hora_mas_veces" to horaMasVecesActualizada,
                "intervalo_horas" to intervaloHorasActualizada,
                "presentacion" to presentacionActualizada,
                "unidad" to unidadActualizada
            )

            db.collection("toma_medicamentos").document(medicamentoId)
                .set(medicamentoActualizado, SetOptions.merge())
                .addOnSuccessListener {
                    //Toast.makeText(this, "Medicamento actualizado correctamente", Toast.LENGTH_SHORT).show()
                    // Redirigir a la actividad de confirmación si no se actualiza la contraseña
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
        val confirm = Intent(this, ConfirmacionEdicionRecordatorioMedicamento::class.java)
        startActivity(confirm)
        finish() // Opcional: Cierra la actividad actual si no quieres volver a ella
    }
}