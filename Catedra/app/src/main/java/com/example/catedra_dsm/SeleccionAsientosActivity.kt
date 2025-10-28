package com.example.catedra_dsm


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import java.text.DecimalFormat

class SeleccionAsientosActivity : AppCompatActivity() {

    // Simulación de datos estáticos
    private val nombrePelicula = "Venom 3"
    private val horaPelicula = "6:30 PM"
    private val formatoPelicula = "2D" // Podría ser 3D
    private val costoBaseAsiento = 8.50 // Ejemplo de precio por asiento

    // El conjunto de asientos seleccionados por el usuario
    private val asientosSeleccionados = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate de que el nombre del layout sea correcto
        setContentView(R.layout.activity_seleccion_asientos) // Usamos 'activity_asientos' como estándar

        // 1. Generar la Sala aleatoria
        val sala = Random.nextInt(1, 6)

        // 2. Definir Views
        val tvResumenPelicula = findViewById<TextView>(R.id.tv_resumen_pelicula)
        val tvResumenSala = findViewById<TextView>(R.id.tv_resumen_sala)
        val tvResumenHora = findViewById<TextView>(R.id.tv_resumen_hora)
        val tvAsientosElegidos = findViewById<TextView>(R.id.tv_asientos_elegidos)
        val tvTotalPagar = findViewById<TextView>(R.id.tv_total_pagar)

        val btnConfirmar = findViewById<Button>(R.id.btn_confirmar_seleccion)
        val btnCambiarPelicula = findViewById<Button>(R.id.btn_cambiar_pelicula)
        val btnAnadirGolosinas = findViewById<Button>(R.id.btn_anadir_golosinas)

        // --- Simulación de asientos seleccionados (Para la demostración de resumen) ---
        // En una app real, esta lista se llenaría al hacer clic en los botones de asientos.
        asientosSeleccionados.addAll(listOf("A5", "A6", "B4"))
        // -----------------------------------------------------------------------------

        // 3. Mostrar Resumen de la Película
        tvResumenPelicula.text = "Película: $nombrePelicula - $formatoPelicula"
        tvResumenSala.text = "Sala: $sala"
        tvResumenHora.text = "Hora: $horaPelicula"

        // 4. Actualizar Resumen de Asientos y Pago
        actualizarResumen(tvAsientosElegidos, tvTotalPagar, btnConfirmar)

        // 5. Configurar Botones

        // Botón Confirmar Selección
        btnConfirmar.setOnClickListener {
            // Aquí iría la lógica para pasar los datos al Activity de Carrito/Pago
            // Asegúrate de que CarritoActivity exista.
            // startActivity(Intent(this, CarritoActivity::class.java))
        }

        // Botón Cambiar Película (Asume que vuelve a la actividad de selección de película)
        btnCambiarPelicula.setOnClickListener {
            finish() // Cierra esta Activity para volver a la anterior
        }

        // Botón Añadir Golosinas (Dirige a Golosinas.xml, cuya Activity es GolosinasActivity)
        btnAnadirGolosinas.setOnClickListener {
            // Asegúrate de que GolosinasActivity exista.
            startActivity(Intent(this, GolosinasActivity::class.java))
        }
    }

    /**
     * Calcula el total a pagar y actualiza los TextViews de resumen.
     */
    private fun actualizarResumen(tvAsientos: TextView, tvTotal: TextView, btnConfirmar: Button) {
        val numAsientos = asientosSeleccionados.size

        // Formatear la lista de asientos (ej: "A5, A6, B4")
        val asientosTexto = if (numAsientos > 0) {
            asientosSeleccionados.sorted().joinToString(", ")
        } else {
            "Ninguno"
        }

        val total = numAsientos * costoBaseAsiento
        val formatter = DecimalFormat("0.00") // Para mostrar el formato de dinero (ej: 25.50)

        // Actualizar vistas
        tvAsientos.text = "Asientos elegidos: $asientosTexto"
        tvTotal.text = "Total a pagar: \$${formatter.format(total)}"

        // El botón de confirmar solo se activa si hay asientos seleccionados
        btnConfirmar.isEnabled = numAsientos > 0
    }
}
