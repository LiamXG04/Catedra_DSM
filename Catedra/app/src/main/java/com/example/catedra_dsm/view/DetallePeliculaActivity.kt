// DetallePeliculaActivity.kt
package com.example.catedra_dsm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.catedra_dsm.R
import com.example.catedra_dsm.controller.RetrofitClient
import com.example.catedra_dsm.model.Movie
import kotlinx.coroutines.launch

class DetallePeliculaActivity : AppCompatActivity() {

    private lateinit var tvTitulo: TextView
    private lateinit var tvGenero: TextView
    private lateinit var tvDuracion: TextView
    private lateinit var tvClasificacion: TextView
    private lateinit var tvSinopsis: TextView
    private lateinit var imgPoster: ImageView
    private lateinit var progress: ProgressBar
    private lateinit var errorView: TextView
    private lateinit var btnAsientos: Button
    private lateinit var btnVolver: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_pelicula)

        tvTitulo = findViewById(R.id.txtTitulo)
        tvGenero = findViewById(R.id.txtGenero)
        tvDuracion = findViewById(R.id.txtDuracion)
        tvClasificacion = findViewById(R.id.txtClasificacion)
        tvSinopsis = findViewById(R.id.txtSinopsis)   // agrega este TextView en el layout
        imgPoster = findViewById(R.id.imgPoster)      // agrega ImageView en el layout si no existe
        progress = findViewById(R.id.progressBar_detalle)
        errorView = findViewById(R.id.tv_error_detalle)
        btnAsientos = findViewById(R.id.btnAsientos)
        btnVolver = findViewById(R.id.btnVolver)


        val movieId = intent.getIntExtra("movie_id", -1)
        if (movieId == -1) {
            finish()
            return
        }

        cargarDetalle(movieId)

        btnAsientos.setOnClickListener {
            startActivity(Intent(this, SeleccionAsientosActivity::class.java))
        }

        btnVolver.setOnClickListener { finish() }
    }

    private fun cargarDetalle(id: Int) {
        progress.visibility = View.VISIBLE
        errorView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instance(this@DetallePeliculaActivity)
                val resp = api.getMovie(id)
                if (resp.isSuccessful) {
                    val m: Movie? = resp.body()
                    progress.visibility = View.GONE

                    if (m == null) {
                        errorView.text = "No se encontró la película."
                        errorView.visibility = View.VISIBLE
                        return@launch
                    }

                    tvTitulo.text = m.title ?: "Sin título"
                    tvGenero.text = "Género: " + (m.genres ?: "N/D")
                    tvDuracion.text = "Duración: " + (m.duration?.let { "$it min" } ?: "N/D")
                    tvClasificacion.text = "Edad: " + (m.rating ?: "N/D")
                    tvSinopsis.text = m.synopsis ?: "Sin sinopsis"

                    val url = m.imageUrl?.let { u ->
                        if (u.startsWith("http", true)) u else "http://10.0.2.2:3000$u"
                    }
                    Glide.with(this@DetallePeliculaActivity)
                        .load(url)
                        .placeholder(R.drawable.placeholder_loading)
                        .error(R.drawable.placeholder_error)
                        .into(imgPoster)

                } else {
                    progress.visibility = View.GONE
                    errorView.text = "Error del servidor: ${resp.code()}"
                    errorView.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                progress.visibility = View.GONE
                errorView.text = "Error de red: ${e.localizedMessage ?: "desconocido"}"
                errorView.visibility = View.VISIBLE
            }
        }
    }
}
