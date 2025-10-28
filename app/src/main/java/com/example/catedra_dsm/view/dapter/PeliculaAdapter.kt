package com.example.catedra_dsm.view.dapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide // Necesitas importar Glide
import com.example.catedra_dsm.R
import com.example.catedra_dsm.model.Movie

// Usamos la data class 'Movie' para Retrofit en lugar de 'Pelicula'
class PeliculaAdapter(private val context: Context, private var movies: List<Movie>) : BaseAdapter() {

    override fun getCount(): Int = movies.size
    override fun getItem(position: Int): Any = movies[position]
    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * FUNCIÓN CORREGIDA: Agregamos updateMovies para que HomeActivity pueda actualizar la lista.
     * Esto resuelve el error "Unresolved reference: updateMovies".
     */
    fun updateMovies(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged() // Notifica al GridView que actualice su contenido
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_pelicula, parent, false)

        val movie = movies[position]

        // Asegúrate que estos IDs (txtTituloPelicula, imgPelicula) existan en item_pelicula.xml
        val titulo = view.findViewById<TextView>(R.id.txtTituloPelicula)
        val imagen = view.findViewById<ImageView>(R.id.imgPelicula)

        titulo.text = movie.title // Usamos 'title' de la data class Movie

        // LÍNEA CRÍTICA: Usamos Glide para cargar la imagen desde la URL de la API
        // Esto reemplaza tu lógica de buscar en "drawable"
        Glide.with(context)
            .load(movie.imageUrl) // Carga la imagen desde la URL de la API (ej: http://10.0.2.2:3000/images/venom3.jpg)
            .placeholder(R.drawable.placeholder_loading) // Asegúrate de tener un placeholder
            .error(R.drawable.placeholder_error)       // Asegúrate de tener un error drawable
            .into(imagen)

        return view
    }
}

