package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConfirmacionEdicionVidaSaludable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion_edicion_vida_saludable)

        // Ocultar la barra de navegación
        val decorView = window.decorView
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        findViewById<LinearLayout>(R.id.ButtonConfirmarEdicionVidaSaludable).setOnClickListener {
            val main = Intent(this, ModuloVidaSaludable::class.java)
            startActivity(main)
            finish()
        }
    }
}