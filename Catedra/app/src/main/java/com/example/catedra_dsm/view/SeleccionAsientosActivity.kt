package com.example.catedra_dsm.view

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.catedra_dsm.R
import java.text.DecimalFormat
import kotlin.random.Random

class SeleccionAsientosActivity : AppCompatActivity() {

    // --- Datos de cabecera (puedes sustituirlos por lo que pases en el Intent) ---
    private val nombrePelicula = "Venom 3"
    private val horaPelicula = "6:30 PM"
    private val formatoPelicula = "2D"
    private val costoBaseAsiento = 8.50

    // --- Config de sala ---
    private val numFilas = 6
    private val numColumnas = 10

    // Estado
    private lateinit var grid: GridLayout
    private lateinit var tvResumenPelicula: TextView
    private lateinit var tvResumenSala: TextView
    private lateinit var tvResumenHora: TextView
    private lateinit var tvAsientosElegidos: TextView
    private lateinit var tvTotalPagar: TextView
    private lateinit var btnConfirmar: Button
    private lateinit var btnCambiarPelicula: Button
    private lateinit var btnAnadirGolosinas: Button

    private val asientosSeleccionados = mutableSetOf<String>()
    private val asientosVendidos = mutableSetOf<String>() // simulación

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate del nombre correcto:
        setContentView(R.layout.activity_seleccion_asientos)

        // 1) Referencias
        grid               = findViewById(R.id.grid_asientos)
        tvResumenPelicula  = findViewById(R.id.tv_resumen_pelicula)
        tvResumenSala      = findViewById(R.id.tv_resumen_sala)
        tvResumenHora      = findViewById(R.id.tv_resumen_hora)
        tvAsientosElegidos = findViewById(R.id.tv_asientos_elegidos)
        tvTotalPagar       = findViewById(R.id.tv_total_pagar)
        btnConfirmar       = findViewById(R.id.btn_confirmar_seleccion)
        btnCambiarPelicula = findViewById(R.id.btn_cambiar_pelicula)
        btnAnadirGolosinas = findViewById(R.id.btn_anadir_golosinas)

        // 2) Cabecera/resumen
        val sala = Random.nextInt(1, 6)
        tvResumenPelicula.text = "Película: $nombrePelicula - $formatoPelicula"
        tvResumenSala.text = "Sala: $sala"
        tvResumenHora.text = "Hora: $horaPelicula"

        // 3) Configurar GridLayout (asegúrate que en XML tengas app:rowCount y app:columnCount,
        //    pero aquí lo seteamos también por si acaso)
        grid.rowCount = numFilas
        grid.columnCount = numColumnas

        // 4) Simular asientos vendidos (en real, esto te puede llegar de API)
        simularVendidos(12) // por ejemplo, 12 vendidos aleatorios

        // 5) Llenar grilla con asientos clicables
        poblarAsientos()

        // 6) Acciones que ya tenías
        btnConfirmar.setOnClickListener {
            // Aquí enviarías asientosSeleccionados a tu flujo de pago / CarritoActivity
            // startActivity(Intent(this, CarritoActivity::class.java))
        }

        btnCambiarPelicula.setOnClickListener { finish() }

        btnAnadirGolosinas.setOnClickListener {
            startActivity(Intent(this, GolosinasActivity::class.java))
        }

        // Inicializa el resumen
        actualizarResumen()
    }

    private fun poblarAsientos() {
        // Limpia por si recargas
        grid.removeAllViews()

        // Dimensiones de cada "asiento" en dp
        val seatSizeDp = 36
        val seatMarginDp = 4

        for (fila in 0 until numFilas) {
            val letraFila = ('A' + fila)
            for (col in 1..numColumnas) {

                val idAsiento = "$letraFila$col"

                val btn = Button(this).apply {
                    text = idAsiento
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    isAllCaps = false
                    // Fondo base
                    background = ContextCompat.getDrawable(this@SeleccionAsientosActivity, R.drawable.seat_bg)
                    // Tinte según estado
                    tintSegunEstado(this, idAsiento, seleccionado = false)

                    setOnClickListener {
                        onTapAsiento(idAsiento, this)
                    }
                }

                val params = GridLayout.LayoutParams().apply {
                    width  = dp(seatSizeDp)
                    height = dp(seatSizeDp)
                    setMargins(dp(seatMarginDp), dp(seatMarginDp), dp(seatMarginDp), dp(seatMarginDp))
                    rowSpec = GridLayout.spec(fila, 1, 1f)
                    columnSpec = GridLayout.spec(col - 1, 1, 1f)
                }

                // Si está vendido, deshabilitar clicks
                if (asientosVendidos.contains(idAsiento)) {
                    btn.isEnabled = false
                }

                grid.addView(btn, params)
            }
        }
    }

    private fun onTapAsiento(idAsiento: String, view: View) {
        // Si está vendido, ignorar
        if (asientosVendidos.contains(idAsiento)) return

        val estabaSeleccionado = asientosSeleccionados.contains(idAsiento)
        if (estabaSeleccionado) {
            asientosSeleccionados.remove(idAsiento)
        } else {
            asientosSeleccionados.add(idAsiento)
        }

        tintSegunEstado(view, idAsiento, seleccionado = !estabaSeleccionado)
        actualizarResumen()
    }

    private fun tintSegunEstado(view: View, idAsiento: String, seleccionado: Boolean) {
        val color = when {
            asientosVendidos.contains(idAsiento) -> R.color.seat_sold
            seleccionado || asientosSeleccionados.contains(idAsiento) -> R.color.seat_selected
            else -> R.color.seat_available
        }
        // Cambia el color del fondo
        val bg = view.background?.mutate()
        bg?.setTint(ContextCompat.getColor(this, color))
        view.background = bg
        // Habilitado solo si no está vendido
        view.isEnabled = !asientosVendidos.contains(idAsiento)
    }

    private fun actualizarResumen() {
        val lista = if (asientosSeleccionados.isEmpty()) "Ninguno"
        else asientosSeleccionados.sorted().joinToString(", ")

        val total = asientosSeleccionados.size * costoBaseAsiento
        val formatter = DecimalFormat("0.00")

        tvAsientosElegidos.text = "Asientos elegidos: $lista"
        tvTotalPagar.text = "Total a pagar: \$${formatter.format(total)}"
        btnConfirmar.isEnabled = asientosSeleccionados.isNotEmpty()
    }

    private fun dp(value: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()

    private fun simularVendidos(cuantos: Int) {
        // Marca asientos aleatorios como vendidos
        val todos = mutableListOf<String>()
        for (fila in 0 until numFilas) {
            val letraFila = ('A' + fila)
            for (col in 1..numColumnas) {
                todos.add("$letraFila$col")
            }
        }
        todos.shuffle()
        asientosVendidos.clear()
        asientosVendidos.addAll(todos.take(cuantos))
    }
}
