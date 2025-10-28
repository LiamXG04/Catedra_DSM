package com.example.catedra_dsm



import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val grid = findViewById<GridView>(R.id.gridPeliculas)
        val peliculas = listOf(
            Pelicula("Venom 3", "Acción", "2h 10m", "B", "drawable/venom3.jpg"),
            Pelicula("Inside Out 2", "Animación", "1h 45m", "A", "drawable/insideout2.jpg"),
            Pelicula("Joker 2", "Drama", "2h 20m", "C", "drawable/joker2.jpg")
        )

        val adapter = PeliculaAdapter(this, peliculas)
        grid.adapter = adapter

        grid.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetallePeliculaActivity::class.java)
            intent.putExtra("titulo", peliculas[position].titulo)
            intent.putExtra("genero", peliculas[position].genero)
            intent.putExtra("duracion", peliculas[position].duracion)
            intent.putExtra("clasificacion", peliculas[position].clasificacion)
            startActivity(intent)
        }
    }
}
