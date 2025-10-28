package com.example.catedra_dsm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.catedra_dsm.view.dapter.CandyAdapter
import com.example.catedra_dsm.R
import com.example.catedra_dsm.controller.RetrofitClient
import com.example.catedra_dsm.model.Candy
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class GolosinasActivity : AppCompatActivity() {

    private lateinit var candyRecyclerView: RecyclerView
    private lateinit var subtotalTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnContinuar: Button
    private lateinit var candyAdapter: CandyAdapter
    private var currentSubtotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_golosinas)

        // Asume IDs en activity_golosinas.xml
        candyRecyclerView = findViewById(R.id.candy_recycler_view)
        subtotalTextView = findViewById(R.id.tv_golosinas_subtotal)
        progressBar = findViewById(R.id.progressBar_golosinas)
        btnContinuar = findViewById(R.id.btn_continuar_a_pago)

        candyRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCandies()

        btnContinuar.setOnClickListener {
            // Ir al Carrito/Pago
            val intent = Intent(this, CarritoActivity::class.java)

            // Pasar la información seleccionada de las golosinas
            val selectedCandiesList = candyAdapter.getSelectedCandies()
                .filter { it.quantity > 0 } // Solo las que tienen cantidad > 0
                .joinToString(", ") { "${it.candy.name} (x${it.quantity})" }

            intent.putExtra("golosinas_resumen", selectedCandiesList)
            intent.putExtra("golosinas_total", currentSubtotal)

            // Aquí debes asegurarte de también pasar los datos de Película y Asientos
            // ... (Datos de asientos y película, asumimos que se pasan desde el inicio del flujo)

            startActivity(intent)
        }
    }

    private fun loadCandies() {
        progressBar.visibility = View.VISIBLE
        candyRecyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instance(this@GolosinasActivity)
                val response = api.getCandies()

                if (response.isSuccessful) {
                    val candies: List<Candy> = response.body() ?: emptyList()

                    // Asegúrate que CandyAdapter.CandyItem recibe un Candy
                    val candyItems: MutableList<CandyAdapter.CandyItem> =
                        candies.map { candy -> CandyAdapter.CandyItem(candy) }.toMutableList()

                    // Asegúrate que el constructor de CandyAdapter es:
                    // CandyAdapter(items: MutableList<CandyItem>, onSubtotalChange: (Double)->Unit)
                    candyAdapter = CandyAdapter(candyItems) { newSubtotal: Double ->
                        updateSubtotal(newSubtotal)
                    }
                    candyRecyclerView.adapter = candyAdapter

                    progressBar.visibility = View.GONE
                    candyRecyclerView.visibility = View.VISIBLE

                    updateSubtotal(0.0)
                } else {
                    showError("Error al cargar menú: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("No hay conexión para cargar las golosinas.")
            }
        }
    }


    private fun updateSubtotal(newSubtotal: Double) {
        currentSubtotal = newSubtotal
        val formatter = DecimalFormat("0.00")
        subtotalTextView.text = "Subtotal: \$${formatter.format(newSubtotal)}"

        // Habilitar botón de pago solo si hay algo seleccionado
        btnContinuar.isEnabled = currentSubtotal > 0
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        // Muestra un Toast o un TextView de error
        // Aquí puedes usar un TextView similar al que usamos en HomeActivity
    }
}