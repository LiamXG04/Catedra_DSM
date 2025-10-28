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

        gridPeliculas.setOnItemClickListener { _, _, position, _ ->
            val movie = peliculasAdapter.getItem(position) // ya es Movie
            val intent = Intent(this, DetallePeliculaActivity::class.java)
            intent.putExtra("movie_id", movie.id)
            startActivity(intent)
        }


        loadMovies()
    }

    private fun loadMovies() {
        progressBar.visibility = View.VISIBLE
        gridPeliculas.visibility = View.GONE
        errorTextView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instance(this@HomeActivity)
                val response = api.getMovies()

                if (response.isSuccessful) {
                    // Si tu API devuelve lista directa:
                    val movies: List<Movie> = response.body() ?: emptyList()

                    // Si devuelve envuelta en { movies: [...] }:
                    // val movies = response.body()?.movies ?: emptyList()

                    peliculasAdapter.updateMovies(movies)

                    progressBar.visibility = View.GONE
                    gridPeliculas.visibility = View.VISIBLE

                    if (movies.isEmpty()) showError("No hay películas en cartelera.")
                } else {
                    showError("Error del servidor: Código ${response.code()}")
                }
            } catch (e: Exception) {
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