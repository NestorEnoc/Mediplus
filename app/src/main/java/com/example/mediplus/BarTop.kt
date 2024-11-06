package com.example.mediplus

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class BarTop : Fragment() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_bar_top, container, false)

        // Obtener las iniciales pasadas desde la actividad
        val usuarioLogeado = arguments?.getString("usuarioLogeado")

        // Encontrar el botón en el layout
        val buttonMenu: Button = view.findViewById(R.id.button_perfil_sesion)

        // Asignar las iniciales al texto del botón
        buttonMenu.text = usuarioLogeado

        // Código para el popup de perfil y cerrar sesión
        buttonMenu.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.popup_perfil, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            // Ajusta el ancho del popup manualmente si lo necesitas
            popupWindow.width = 270 // Ajusta el ancho en píxeles o dp

            // Maneja los clics en las opciones
            popupView.findViewById<TextView>(R.id.verPerfil).setOnClickListener {
                val intent = Intent(context, Perfil::class.java)
                startActivity(intent)
                popupWindow.dismiss()
            }

            popupView.findViewById<TextView>(R.id.cerrarSesion).setOnClickListener {
                Toast.makeText(context, "Eliminar seleccionado", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            // Posicionar el popup alineado a la derecha del botón
            val offsetX = buttonMenu.width - popupWindow.width
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupWindow.showAsDropDown(buttonMenu, offsetX, 50)
            } else {
                popupWindow.showAsDropDown(buttonMenu)
            }
        }

        // Código para el popup del menú lateral
        val menuButton: LinearLayout = view.findViewById(R.id.menu) // Asegúrate de que este ID exista en tu layout

        // Inflar el layout del menú lateral
        val menuView = layoutInflater.inflate(R.layout.menu_lateral, null)

        // Crear el PopupWindow para el menú lateral
        val menuPopupWindow = PopupWindow(
            menuView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )

        // Configurar clic en el botón de menú para mostrar el PopupWindow
        menuButton.setOnClickListener {
            // Mostrar el popup junto al botón de menú o en el inicio de la pantalla
            menuPopupWindow.showAtLocation(view, Gravity.START, 0, 0)

            // Cerrar el PopupWindow al hacer clic en el fondo
            menuPopupWindow.isOutsideTouchable = true
            menuPopupWindow.isFocusable = true

            // Establecer el fondo del PopupWindow para que pueda detectar clics
            //menuPopupWindow.setBackgroundDrawable(ColorDrawable(0x80000000.toInt()))

            // Añadir un listener para cerrar el menú al hacer clic en cualquier parte
            menuView.setOnTouchListener { _, _ ->
                menuPopupWindow.dismiss()
                true // Indica que el evento fue manejado
            }

            // Configurar los listeners para cada opción del menú
            menuView.findViewById<LinearLayout>(R.id.tomaMedicamentos).setOnClickListener {
                Toast.makeText(context, "Toma de medicamentos seleccionada", Toast.LENGTH_SHORT).show()
            }
            menuView.findViewById<LinearLayout>(R.id.alertaAbastecimiento).setOnClickListener {
                Toast.makeText(context, "Alerta de abastecimiento seleccionada", Toast.LENGTH_SHORT).show()
            }
            menuView.findViewById<LinearLayout>(R.id.citasMedicas).setOnClickListener {
                Toast.makeText(context, "Citas médicas seleccionada", Toast.LENGTH_SHORT).show()
            }
            menuView.findViewById<LinearLayout>(R.id.gestionSalud).setOnClickListener {
                Toast.makeText(context, "Gestión de salud seleccionada", Toast.LENGTH_SHORT).show()
            }
            menuView.findViewById<LinearLayout>(R.id.vidaSaludable).setOnClickListener {
                Toast.makeText(context, "Vida saludable seleccionada", Toast.LENGTH_SHORT).show()
            }
            menuView.findViewById<LinearLayout>(R.id.moduloExamenes).setOnClickListener {
                Toast.makeText(context, "Exámenes seleccionada", Toast.LENGTH_SHORT).show()
            }
            menuView.findViewById<LinearLayout>(R.id.moduloHistoriales).setOnClickListener {
                Toast.makeText(context, "Historiales seleccionada", Toast.LENGTH_SHORT).show()
            }
            menuView.findViewById<LinearLayout>(R.id.moduloNotasMedicamentos).setOnClickListener {
                Toast.makeText(context, "Notas de medicamentos seleccionada", Toast.LENGTH_SHORT).show()
            }
            menuView.findViewById<LinearLayout>(R.id.moduloOtrasNotas).setOnClickListener {
                Toast.makeText(context, "Otras notas generales seleccionada", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = BarTop()
    }
}

