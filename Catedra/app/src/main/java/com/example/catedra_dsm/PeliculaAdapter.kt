package com.example.catedra_dsm



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class PeliculaAdapter(private val context: Context, private val peliculas: List<Pelicula>) : BaseAdapter() {

    override fun getCount(): Int = peliculas.size
    override fun getItem(position: Int): Any = peliculas[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_pelicula, parent, false)

        val pelicula = peliculas[position]

        val titulo = view.findViewById<TextView>(R.id.txtTituloPelicula)
        val imagen = view.findViewById<ImageView>(R.id.imgPelicula)

        titulo.text = pelicula.titulo

        // Aquí puedes cambiar las imágenes reales cuando las tengas en drawable
        val resId = context.resources.getIdentifier(
            pelicula.imagen.replace("drawable/", "").replace(".jpg", "").replace(".png", ""),
            "drawable", context.packageName
        )
        imagen.setImageResource(resId)

        return view
    }
}
