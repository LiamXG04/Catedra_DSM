package com.example.catedra_dsm.view.dapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.catedra_dsm.R
import com.example.catedra_dsm.model.Candy

class CandyAdapter(
    private val candyList: MutableList<CandyItem>,
    private val onSubtotalChanged: (Double) -> Unit // Callback para actualizar el total en la Activity
) : RecyclerView.Adapter<CandyAdapter.CandyViewHolder>() {

    // Clase auxiliar para manejar la cantidad seleccionada
    data class CandyItem(val candy: Candy, var quantity: Int = 0)

    private val selectedCandies = mutableMapOf<Int, CandyItem>() // Mapa para seguir la selección

    inner class CandyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_candy_name)
        val price: TextView = itemView.findViewById(R.id.tv_candy_price)
        val quantity: TextView = itemView.findViewById(R.id.tv_candy_quantity)
        val image: ImageView = itemView.findViewById(R.id.iv_candy_image)
        val btnAdd: Button = itemView.findViewById(R.id.btn_add)
        val btnRemove: Button = itemView.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_candy, parent, false)
        return CandyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandyViewHolder, position: Int) {
        val item = candyList[position]
        holder.name.text = item.candy.name
        holder.price.text = String.format("+\$%.2f", item.candy.price)
        holder.quantity.text = item.quantity.toString()

        Glide.with(holder.itemView.context).load(item.candy.imageUrl).into(holder.image)

        holder.btnAdd.setOnClickListener {
            updateQuantity(item, 1)
            holder.quantity.text = item.quantity.toString()
        }

        holder.btnRemove.setOnClickListener {
            if (item.quantity > 0) {
                updateQuantity(item, -1)
                holder.quantity.text = item.quantity.toString()
            }
        }
    }

    override fun getItemCount(): Int = candyList.size

    // Lógica principal de actualización y cálculo
    private fun updateQuantity(item: CandyItem, delta: Int) {
        item.quantity += delta

        if (item.quantity > 0) {
            selectedCandies[item.candy.id] = item
        } else {
            selectedCandies.remove(item.candy.id)
        }

        // Calcular el subtotal de todas las golosinas seleccionadas
        val newSubtotal = selectedCandies.values.sumOf { it.candy.price * it.quantity }
        onSubtotalChanged(newSubtotal)
    }

    // Método para obtener la lista final de golosinas seleccionadas
    fun getSelectedCandies(): List<CandyItem> {
        return selectedCandies.values.toList()
    }
}