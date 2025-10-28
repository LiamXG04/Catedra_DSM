package com.example.catedra_dsm.view

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.catedra_dsm.R
import java.text.DecimalFormat

class CarritoActivity : AppCompatActivity() {

    private lateinit var tvResumenFinal: TextView
    private lateinit var btnConfirmar: Button

    // Simulación de una Sala, ya que no se selecciona en el flujo actual
    private val sala = "Sala 4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // 1. Obtener los datos del Intent (provenientes de SeleccionAsientosActivity y GolosinasActivity)
        val peliculaTitulo = intent.getStringExtra("movie_title") ?: "N/A"
        val hora = intent.getStringExtra("show_time") ?: "N/A"

        // Datos de Asientos
        val asientosTotal = intent.getDoubleExtra("asientos_total", 0.0)
        val asientosResumen = intent.getStringArrayListExtra("selected_seats")?.joinToString(", ") ?: "Ninguno"

        // Datos de Golosinas
        val golosinasTotal = intent.getDoubleExtra("golosinas_total", 0.0)
        val golosinasResumen = intent.getStringExtra("golosinas_resumen") ?: "Ninguna"

        // 2. Calcular el Total Final
        val totalFinal = asientosTotal + golosinasTotal
        val formatter = DecimalFormat("0.00")

        // 3. Inicializar Vistas
        tvResumenFinal = findViewById(R.id.tv_resumen_final)
        btnConfirmar = findViewById(R.id.btn_confirmar_y_pagar)

        // 4. Construir el resumen de la factura
        val resumenTexto = """
            *** RESUMEN DE COMPRA ***
            
            🎬 Película: ${peliculaTitulo}
            
            🕒 Hora: ${hora} | Sala: ${sala}
            
            ---
            
            🪑 Asientos (${asientosResumen.split(",").size} seleccionados):
            ${asientosResumen}
            Subtotal Asientos: \$${formatter.format(asientosTotal)}
            
            ---
            
            🍿 Golosinas:
            ${golosinasResumen}
            Subtotal Golosinas: \$${formatter.format(golosinasTotal)}
            
            ========================
            💰 TOTAL A PAGAR: \$${formatter.format(totalFinal)}
            ========================
        """.trimIndent()

        tvResumenFinal.text = resumenTexto

        // 5. Lógica del botón de Confirmar
        btnConfirmar.setOnClickListener {
            // Aquí simulas la llamada final a un endpoint de pago o confirmación
            Toast.makeText(this, "¡Pago de \$${formatter.format(totalFinal)} confirmado con éxito!", Toast.LENGTH_LONG).show()
            // Podrías volver a la HomeActivity
            // startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}