package com.example.mediplus

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mediplus.inicio_sesion.Login
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: LinearLayout
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Recuperar el userId de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: ""

        /* Codigo para traer las iniciales del usuario logueado*/
        val usuarioLogeado = sharedPreferences.getString("usuarioLogeado", "")

        // Encontrar el botón en el layout
        val buttonMenu: Button = findViewById(R.id.button_perfil_sesion)

        // Asignar las iniciales al texto del botón
        buttonMenu.text = usuarioLogeado
        /**/
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
                val intent = Intent(this, Perfil::class.java)
                startActivity(intent)
                popupWindow.dismiss()
            }

            popupView.findViewById<TextView>(R.id.cerrarSesion).setOnClickListener {
                showCloseSesionConfirmationDialog(this) {
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
        /**/

        // Boton de notificaciones
        val boton_notificaciones = findViewById<ImageView>(R.id.button_notificaciones)
        boton_notificaciones.setOnClickListener {
            startActivity(Intent(this, ModuloMedicamentos::class.java))
        }

        /**/

        val recordatorioMedicamentos = findViewById<CardView>(R.id.recordatorioMedicamentos)

        recordatorioMedicamentos.setOnClickListener {
            startActivity(Intent(this, ModuloMedicamentos::class.java))
        }

        val recordatorioCitas = findViewById<CardView>(R.id.recordatorioCitas)

        recordatorioCitas.setOnClickListener {
            startActivity(Intent(this, ModuloCitas::class.java))
        }

        val recordatorioHidratacion = findViewById<CardView>(R.id.recordatorioHidratacion)

        recordatorioHidratacion.setOnClickListener {
            startActivity(Intent(this, ModuloVidaSaludable::class.java))
        }

        val recordatorioAnotaciones = findViewById<CardView>(R.id.recordatorioAnotaciones)

        recordatorioAnotaciones.setOnClickListener {
            startActivity(Intent(this, ModuloNotasMedicamentos::class.java))
        }

        val recordatorioExamenes = findViewById<CardView>(R.id.recordatorioExamenes)

        recordatorioExamenes.setOnClickListener {
            startActivity(Intent(this, ModuloExamenes::class.java))
        }


        // Agregar el fragmento de la bottom bar
        val fragment = BarBottom()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_bottom_bar, fragment)
            .commit()


        //llamarTopBar()

        drawerLayout = findViewById(R.id.drawer_layout_main)
        navigationView = findViewById(R.id.navigation_view_main)
        toolbar = findViewById(R.id.menu_lateral_main)
        //val menuIcon: ImageView = findViewById(R.id.icono_de_menu)

        // Configuración del Navigation Drawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Manejar clic en los ítems del menú
            when (menuItem.itemId) {
                R.id.tomaMedicamentosMenu -> {
                    startActivity(Intent(this, ModuloMedicamentos::class.java))
                }
                R.id.alertaAbastecimientoMenu -> {
                    startActivity(Intent(this, ModuloAlertaAbastecimiento::class.java))
                }
                R.id.citasMedicasMenu -> {
                    startActivity(Intent(this, ModuloCitas::class.java))
                }
                R.id.gestionSaludMenu -> {
                    startActivity(Intent(this, ModuloGestionSalud::class.java))
                }
                R.id.vidaSaludableMenu -> {
                    startActivity(Intent(this, ModuloVidaSaludable::class.java))
                }
                R.id.examenesMenu -> {
                    startActivity(Intent(this, ModuloExamenes::class.java))
                }
                R.id.historialesMenu -> {
                    startActivity(Intent(this, ModuloHistoriales::class.java))
                }
                R.id.notasMedicamentosMenu -> {
                    startActivity(Intent(this, ModuloNotasMedicamentos::class.java))
                }
                R.id.otrasNotasMenu -> {
                    startActivity(Intent(this, ModuloNotasGenerales::class.java))
                }
            }
            // Cerrar el drawer después de seleccionar un ítem
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Abrir el Navigation Drawer al hacer clic en el icono del menú
        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }

    override fun onBackPressed() {
        // Cerrar el drawer si está abierto al presionar el botón de atrás
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Finalizar la actividad actual
        finish()
    }

    /*fun llamarTopBar() {
        // Obtener el valor de usuarioLogeado desde tu lógica de inicio de sesión
        val usuarioLogeado = intent.getStringExtra("usuarioLogeado") // Asegúrate de que este valor esté disponible

        // Crear una nueva instancia del fragmento
        val fragment = BarTop().apply {
            arguments = Bundle().apply {
                putString("usuarioLogeado", usuarioLogeado) // Pasa el usuario logueado como argumento
            }
        }

        // Agregar el fragmento a la actividad
        supportFragmentManager.beginTransaction()
            .replace(R.id.myFragmentContainer, fragment) // Asegúrate de que este ID coincida con el contenedor en tu layout
            .commit()
    }*/
}