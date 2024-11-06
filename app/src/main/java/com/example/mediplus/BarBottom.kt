package com.example.mediplus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class BarBottom : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        // Obtén el BottomNavigationView y configura el listener
        val view = inflater.inflate(R.layout.fragment_bar_bottom, container, false)

        // Referenciar el `LinearLayout` para los botones
        val inicio = view.findViewById<LinearLayout>(R.id.inicio)
        val medicina = view.findViewById<LinearLayout>(R.id.medicina)
        val citas = view.findViewById<LinearLayout>(R.id.citas)
        val examenes = view.findViewById<LinearLayout>(R.id.examenes)

        // Configurar los listeners de clic
        inicio.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }

        medicina.setOnClickListener {
            startActivity(Intent(requireContext(), ModuloMedicamentos::class.java))
        }

        citas.setOnClickListener {
            startActivity(Intent(requireContext(), ModuloCitas::class.java))
        }

        examenes.setOnClickListener {
            startActivity(Intent(requireContext(), ModuloExamenes::class.java))
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = BarBottom()
    }
}