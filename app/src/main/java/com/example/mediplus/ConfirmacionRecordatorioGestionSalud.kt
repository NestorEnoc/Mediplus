package com.example.mediplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConfirmacionRecordatorioGestionSalud : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion_recordatorio_gestion_salud)

        // Ocultar la barra de navegación
        val decorView = window.decorView
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        findViewById<LinearLayout>(R.id.confirmarRecordatorioGestionSalud).setOnClickListener {
            val main = Intent(this, ModuloGestionSalud::class.java)
            startActivity(main)
            finish()
        }
    }
}