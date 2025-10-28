package com.example.catedra_dsm.view.dapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.catedra_dsm.R
import com.example.catedra_dsm.model.Movie

class PeliculaAdapter(
    private val context: Context,
    private var movies: List<Movie>
) : BaseAdapter() {

    override fun getCount(): Int = movies.size
    override fun getItem(position: Int): Movie = movies[position]
    override fun getItemId(position: Int): Long = movies[position].id.toLong()

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: VH
        val view: View = if (convertView == null) {
            val v = LayoutInflater.from(context).inflate(R.layout.item_pelicula, parent, false)
            holder = VH(
                v.findViewById(R.id.txtTituloPelicula),
                v.findViewById(R.id.imgPelicula)
            )
            v.tag = holder
            v
        } else {
            holder = convertView.tag as VH
            convertView
        }

        val movie = movies[position]

        holder.titulo.text = movie.title?.takeIf { it.isNotBlank() } ?: "Sin título"


        val url = movie.imageUrl?.let { u ->
            if (u.startsWith("http", ignoreCase = true)) u else "http://10.0.2.2:3000$u"
        }

        Glide.with(view) // usar la view ayuda al ciclo de vida del item
            .load(url)
            .placeholder(R.drawable.placeholder_loading)
            .error(R.drawable.placeholder_error)
            .into(holder.imagen)

        return view
    }


    private data class VH(
        val titulo: TextView,
        val imagen: ImageView
    )
}
