package com.example.catedra_dsm



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetallePeliculaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_pelicula)

        val titulo = intent.getStringExtra("titulo")
        val genero = intent.getStringExtra("genero")
        val duracion = intent.getStringExtra("duracion")
        val clasificacion = intent.getStringExtra("clasificacion")

        findViewById<TextView>(R.id.txtTitulo).text = titulo
        findViewById<TextView>(R.id.txtGenero).text = "Género: $genero"
        findViewById<TextView>(R.id.txtDuracion).text = "Duración: $duracion"
        findViewById<TextView>(R.id.txtClasificacion).text = "Edad: $clasificacion"
        findViewById<TextView>(R.id.txtReparto).text =
            "Reparto: Tom Hardy, Zendaya, Keanu Reeves, Margot Robbie"
        findViewById<TextView>(R.id.txtEstreno).text = "Estreno: 20 de Octubre de 2025"

        val btnAsientos = findViewById<Button>(R.id.btnAsientos)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        btnAsientos.setOnClickListener {
            startActivity(Intent(this, SeleccionAsientosActivity::class.java))
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}
