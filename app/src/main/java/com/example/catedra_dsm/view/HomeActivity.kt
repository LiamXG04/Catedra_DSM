package com.example.catedra_dsm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.catedra_dsm.view.dapter.PeliculaAdapter
import com.example.catedra_dsm.R
import com.example.catedra_dsm.model.Movie
import com.example.catedra_dsm.controller.RetrofitClient
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var gridPeliculas: GridView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var peliculasAdapter: PeliculaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Asume que activity_home tiene un GridView con id gridPeliculas,
        // un ProgressBar con id progressBar_loading, y un TextView con id tv_error
        gridPeliculas = findViewById(R.id.gridPeliculas)
        progressBar = findViewById(R.id.progressBar_loading)
        errorTextView = findViewById(R.id.tv_error)

        // Inicializamos el Adapter con una lista vacía
        peliculasAdapter = PeliculaAdapter(this, emptyList())
        gridPeliculas.adapter = peliculasAdapter

        // Manejar el click para ir a Detalles, pasando el ID de la película
        gridPeliculas.setOnItemClickListener { _, _, position, _ ->
            val movie = peliculasAdapter.getItem(position) as Movie
            val intent = Intent(this, DetallePeliculaActivity::class.java)
            intent.putExtra("movie_id", movie.id) // Pasamos el ID para cargar los detalles
            startActivity(intent)
        }

        loadMovies()
    }

    private fun loadMovies() {
        // 1. Mostrar estado de carga
        progressBar.visibility = View.VISIBLE
        gridPeliculas.visibility = View.GONE
        errorTextView.visibility = View.GONE

        // 2. Ejecutar la llamada API en un Coroutine
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMovies()

                if (response.isSuccessful) {
                    val movies = response.body()?.movies ?: emptyList()

                    // Asegúrate de que tu adapter tenga un método para actualizar la lista
                    peliculasAdapter.updateMovies(movies)

                    progressBar.visibility = View.GONE
                    gridPeliculas.visibility = View.VISIBLE

                    if (movies.isEmpty()) {
                        showError("No hay películas en cartelera.")
                    }

                } else {
                    // Manejo de error del servidor (ej. 404, 500)
                    showError("Error del servidor: Código ${response.code()}")
                }
            } catch (e: Exception) {
                // Manejo de error de conexión (Sin Internet)
                showError("No hay conexión a Internet o error en el servidor. Inténtalo de nuevo.")
            }
        }
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        gridPeliculas.visibility = View.GONE
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }
}