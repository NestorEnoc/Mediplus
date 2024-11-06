package com.example.mediplus

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.example.mediplus.inicio_sesion.Login
import com.google.firebase.auth.FirebaseAuth

class BarTopReturn : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        auth = FirebaseAuth.getInstance()

        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_bar_top_return, container, false)

        // Obtener las iniciales pasadas desde la actividad
        val usuarioLogeado = arguments?.getString("usuarioLogeado")

        // Encontrar el botón en el layout
        val buttonMenu: Button = view.findViewById(R.id.button_perfil_cerrar_sesion)

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
                showCloseSesionConfirmationDialog(requireActivity()) {
                    cerrarSesion()
                }
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

        val boton_regregar = view.findViewById<ImageView>(R.id.bottom_return)
        boton_regregar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val boton_notificaciones = view.findViewById<ImageView>(R.id.button_notificaciones)
        boton_notificaciones.setOnClickListener {
            startActivity(Intent(requireActivity(), ModuloMedicamentos::class.java))
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = BarTopReturn()
    }

    // Ventana Modal de cerrar sesion
    private fun showCloseSesionConfirmationDialog(context: Context, onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirmar_cerrar_sesion, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.btn_cancelar_sesion).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_cerrar_sesion).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }

    // Metodo de Cerrar sesión
    private fun cerrarSesion() {
        auth.signOut()

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(requireActivity(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Finalizar la actividad actual
        requireActivity().finish()
    }

}