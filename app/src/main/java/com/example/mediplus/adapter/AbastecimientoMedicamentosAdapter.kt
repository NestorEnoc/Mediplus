package com.example.mediplus.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mediplus.ConfirmacionArchivoAbastecimientoMedicamento
import com.example.mediplus.R
import com.example.mediplus.ReabastecerMedicamentos
import com.example.mediplus.VerInfoModuloAbastecimiento
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

data class AbastecimientoMedicamento(
    val id_usuario: String,
    val nombre_medicamento: String,
    val fecha_abastecimiento: String,
    val cantidad: String
)

class AbastecimientoMedicamentoAdapter(private val abastecimiento: List<AbastecimientoMedicamento>) : RecyclerView.Adapter<AbastecimientoMedicamentoAdapter.AbastecimientoMedicamentoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbastecimientoMedicamentoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_listar_abastecimiento, parent, false)
        return AbastecimientoMedicamentoViewHolder(view)
    }

    class AbastecimientoMedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.txtMedicamentoAlertaAbastecimiento)
        private val fechaTextView: TextView = itemView.findViewById(R.id.txtFechaAbastecimientoMedicamentos)
        private val dosisTextView: TextView = itemView.findViewById(R.id.txtUnidades)
        val buttonMenuPuntos: MaterialCardView = itemView.findViewById(R.id.btn_reabastecer)
        val buttonMas: MaterialCardView = itemView.findViewById(R.id.btn_mas)

        fun bind(abastecer: AbastecimientoMedicamento) {
            nombreTextView.text = abastecer.nombre_medicamento
            fechaTextView.text = abastecer.fecha_abastecimiento
            dosisTextView.text = abastecer.cantidad
        }
    }
    override fun onBindViewHolder(holder: AbastecimientoMedicamentoViewHolder, position: Int) {
        holder.bind(abastecimiento[position])

        val context = holder.itemView.context
        val medicamento = abastecimiento[position]
        // Manejo del clic en el botón de tres puntos
        holder.buttonMenuPuntos.setOnClickListener { view ->

            // Crear un Intent para redirigir a la nueva Activity
            val intent = Intent(context, ReabastecerMedicamentos::class.java).apply {
                putExtra("id_usuario", medicamento.id_usuario)
                putExtra("medicamento", medicamento.nombre_medicamento)
                putExtra("cantidad", medicamento.cantidad)
            }
            context.startActivity(intent)
        }

        // Manejo del clic en el botón de tres puntos
        holder.buttonMas.setOnClickListener { view ->
            val intent = Intent(context, VerInfoModuloAbastecimiento::class.java).apply {
                putExtra("id_usuario", medicamento.id_usuario)
                putExtra("medicamento", medicamento.nombre_medicamento)
                putExtra("cantidad", medicamento.cantidad)
                putExtra("fecha_abastecimiento", medicamento.fecha_abastecimiento)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = abastecimiento.size

}