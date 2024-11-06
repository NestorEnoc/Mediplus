package com.example.mediplus

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class CrearModuloMedicamentos : AppCompatActivity() {

    private var isFormPart1Visible = true
    private var isButton1Active = false
    private var isButton2Active = false
    private lateinit var editTextDate: EditText
    private lateinit var editTextHasta: EditText
    private lateinit var btnSelectTime: CardView
    private lateinit var txtSelectedTime: TextView
    private lateinit var masVecesAlDia: CardView
    private lateinit var masVecesAlDiatxtTime: TextView
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_modulo_medicamentos)

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

        // Inicializar las vistas y configurar los click listeners
        val formPart1 = findViewById<LinearLayout>(R.id.formPart1)
        val formPart2 = findViewById<LinearLayout>(R.id.formPart2)
        val primerButton = findViewById<TextView>(R.id.nextButton)
        val segundoButton = findViewById<TextView>(R.id.nextButton2)

        // Configuración inicial
        formPart1.visibility = View.VISIBLE
        formPart2.visibility = View.GONE

        // Configurar el click listener para el primer botón
        primerButton.setOnClickListener {
            // Alterna la visibilidad de formPart2 y oculta formPart1
            formPart2.visibility = if (formPart2.visibility == View.GONE) View.VISIBLE else View.VISIBLE
            formPart1.visibility = View.GONE // Asegúrate de ocultar el primer formulario

            if (!isButton1Active) {
                primerButton.setBackgroundResource(R.drawable.switch_button_part_2) // Cambia el color al activarlo
                segundoButton.setBackgroundResource(R.drawable.switch_button_part_1) // Cambia el color del otro botón para "desactivarlo"
                //isButton1Active = true
                //isButton2Active = false
            }
        }

        // Configurar el click listener para el segundo botón
        segundoButton.setOnClickListener {
            // Alterna la visibilidad de formPart1 y oculta formPart2
            formPart1.visibility = if (isFormPart1Visible) View.VISIBLE else View.VISIBLE
            formPart2.visibility = View.GONE // Asegúrate de ocultar el segundo formulario
            isFormPart1Visible = !isFormPart1Visible

            if (!isButton2Active) {
                segundoButton.setBackgroundResource(R.drawable.switch_button_part_2) // Cambia el color al activarlo
                primerButton.setBackgroundResource(R.drawable.switch_button_part_1) // Cambia el color del otro botón para "desactivarlo"
                //isButton2Active = true
                //isButton1Active = false
            }
        }

        val buttonContinuar = findViewById<TextView>(R.id.continuar)

        buttonContinuar.setOnClickListener {
            //toggleVisibility(formPart1, formPart2)
            // Alterna la visibilidad de formPart2 y oculta formPart1
            formPart2.visibility = if (formPart2.visibility == View.GONE) View.VISIBLE else View.VISIBLE
            formPart1.visibility = View.GONE // Asegúrate de ocultar el primer formulario
            primerButton.setBackgroundResource(R.drawable.switch_button_part_2)
            segundoButton.setBackgroundResource(R.drawable.switch_button_part_1)
        }

        // para el select de presentación del medicamento
        // Encuentra el MaterialAutoCompleteTextView en el layout
        val materialAutoCompleteTextView: MaterialAutoCompleteTextView = findViewById(R.id.seleccionMedicamento)

        // Define las opciones del dropdown
        val opciones = arrayOf("Pastillas", "Tabletas", "Jarabe", "Granulados", "Ampollas", "Cremas o pomadas", "Inhaladores", "Otra presentación")

        // Crea un ArrayAdapter con las opciones
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opciones)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        materialAutoCompleteTextView.setAdapter(adapter)


        // Para el select de unidad
        val seleccioneUnidad: MaterialAutoCompleteTextView = findViewById(R.id.seleccionUnidad)

        // Define las opciones del dropdown
        val opcionesUnidad = arrayOf("50-100 mg", "500 mg", "200-400 mg", "400-1000 UI", "5-20 mg", "Otra medida")

        // Crea un ArrayAdapter con las opciones
        val adapterArray = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesUnidad)

        // Asigna el adaptador al MaterialAutoCompleteTextView
        seleccioneUnidad.setAdapter(adapterArray)


        // Para el selector de fecha
        editTextDate = findViewById(R.id.fechaDesde)

        // Configurar el listener para el EditText
        editTextDate.setOnClickListener {
            showDatePickerDialog(editTextDate)
        }

        editTextHasta = findViewById(R.id.fechaHasta)
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
        btnSelectTime = findViewById(R.id.btn_select_time)
        txtSelectedTime = findViewById(R.id.txt_selected_time)

        btnSelectTime.setOnClickListener {
            showTimePickerDialog(btnSelectTime, txtSelectedTime)
        }

        masVecesAlDia = findViewById(R.id.btnMasVecesAlDia)
        masVecesAlDiatxtTime = findViewById(R.id.txtMostarHoraMasVeces)

        masVecesAlDia.setOnClickListener {
            showTimePickerDialog(masVecesAlDia, masVecesAlDiatxtTime)
        }

        val btnRegistrar = findViewById<LinearLayout>(R.id.btnCrearRecordatorioMedicamentos)
        btnRegistrar.setOnClickListener {
            registrarMedicamento()
        }

    }

    // Método que alterna la visibilidad de los formularios
    /*private fun toggleVisibility(formPart1: LinearLayout, formPart2: LinearLayout) {
        formPart1.visibility = if (isFormPart1Visible) View.GONE else View.VISIBLE
        formPart2.visibility = if (isFormPart1Visible) View.VISIBLE else View.GONE
        isFormPart1Visible = !isFormPart1Visible
    }*/

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

    /*private fun registrarMedicamento() {
        val db = FirebaseFirestore.getInstance()

        // Obtener los nuevos datos de los EditText (cambia los IDs según tu layout)
        val medicamento = findViewById<EditText>(R.id.txtCrearNombreMedicamento).text.toString()
        val presentacion = findViewById<EditText>(R.id.seleccionMedicamento).text.toString()
        val fechaDesde = findViewById<EditText>(R.id.fechaDesde).text.toString()
        val fechaHasta = findViewById<EditText>(R.id.fechaHasta).text.toString()
        val dosis = findViewById<EditText>(R.id.txtDosis).text.toString()
        val unidad = findViewById<EditText>(R.id.seleccionUnidad).text.toString()
        val hora = findViewById<TextView>(R.id.txt_selected_time).text.toString()
        val intervaloHoras = findViewById<EditText>(R.id.txtIntervaloHoras).text.toString()
        val horaMasveces = findViewById<TextView>(R.id.txtMostarHoraMasVeces).text.toString()
        val descripcion = findViewById<EditText>(R.id.txtDescripcion).text.toString()

        // Crear un mapa con los datos a registrar en Firestore
        val medicamentoData = hashMapOf(
            "id_usuario " to userId,
            "nombre_medicamento" to medicamento,
            "presentacion" to presentacion,
            "fecha_desde" to fechaDesde,
            "fecha_hasta" to fechaHasta,
            "num_dosis" to dosis,
            "unidad" to unidad,
            "hora" to hora,
            "intervalo_horas" to intervaloHoras,
            "hora_mas_veces" to horaMasveces,
            "descripcion" to descripcion,
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("toma_medicamentos")
        .add(medicamentoData)  // Usa .add para generar un ID único automáticamente
        .addOnSuccessListener { documentReference ->
            // Mensaje de éxito
            Toast.makeText(this, "Medicamento registrado con éxito: ${documentReference.id}", Toast.LENGTH_SHORT).show()

            // Redirigir a otra actividad si es necesario
            //val confirmar = Intent(this, ConfirmacionMedicamentoRegistrado::class.java)
            //startActivity(confirmar)
            //finish()
        }
        .addOnFailureListener { e ->
        Toast.makeText(this, "Error al registrar medicamento: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun registrarMedicamento() {
        val db = FirebaseFirestore.getInstance()

        // Obtener los nuevos datos de los EditText (cambia los IDs según tu layout)
        val medicamento = findViewById<EditText>(R.id.txtCrearNombreMedicamento).text.toString()
        val presentacion = findViewById<EditText>(R.id.seleccionMedicamento).text.toString()
        val fechaDesde = findViewById<EditText>(R.id.fechaDesde).text.toString()
        val fechaHasta = findViewById<EditText>(R.id.fechaHasta).text.toString()
        val dosis = findViewById<EditText>(R.id.txtDosis).text.toString()
        val unidad = findViewById<EditText>(R.id.seleccionUnidad).text.toString()
        val hora = findViewById<TextView>(R.id.txt_selected_time).text.toString()
        val intervaloHoras = findViewById<EditText>(R.id.txtIntervaloHoras).text.toString()
        val horaMasveces = findViewById<TextView>(R.id.txtMostarHoraMasVeces).text.toString()
        val descripcion = findViewById<EditText>(R.id.txtDescripcion).text.toString()

        // Crear una referencia al documento de usuario
        val usuarioRef = db.document("/users/$userId")

        // Crear un mapa con los datos a registrar en Firestore
        val medicamentoData = hashMapOf(
            "id_usuario" to usuarioRef,  // Usamos la referencia en lugar de solo el userId
            "nombre_medicamento" to medicamento,
            "presentacion" to presentacion,
            "fecha_desde" to fechaDesde,
            "fecha_hasta" to fechaHasta,
            "num_dosis" to dosis,
            "unidad" to unidad,
            "hora" to hora,
            "intervalo_horas" to intervaloHoras,
            "hora_mas_veces" to horaMasveces,
            "descripcion" to descripcion,
        )

        // Registrar el documento del medicamento en Firestore
        db.collection("toma_medicamentos")
            .add(medicamentoData)  // Usa .add para generar un ID único automáticamente
            .addOnSuccessListener { documentReference ->
                // Mensaje de éxito
                //Toast.makeText(this, "Medicamento registrado con éxito: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                // Redirigir a la actividad de confirmación si no se actualiza la contraseña
                redirectToConfirmation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar medicamento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Método para redirigir a la actividad de confirmación
    private fun redirectToConfirmation() {
        val confirmar = Intent(this, ConfirmacionRecordatorioMedicamento::class.java)
        startActivity(confirmar)
        finish() // Opcional: Cierra la actividad actual si no quieres volver a ella
    }
}
