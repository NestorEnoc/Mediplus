package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mediplus.inicio_sesion.Login

class SplashActivity : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // Tiempo de espera (3 segundos)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar la barra de navegación
        val decorView = window.decorView
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        Handler().postDelayed({
            // Iniciar la actividad de inicio de sesión
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Finaliza el splash screen activity
        }, splashTimeOut)
    }
}
