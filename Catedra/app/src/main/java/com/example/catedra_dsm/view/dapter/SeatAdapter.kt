package com.example.catedra_dsm.view.dapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.catedra_dsm.R
import com.example.catedra_dsm.model.Asiento
import com.example.catedra_dsm.model.EstadoSeat

class SeatAdapter(
    private val seats: MutableList<Asiento>,
    private val numCols: Int,
    private val onSeatTap: (seat: Asiento, position: Int) -> Unit
) : RecyclerView.Adapter<SeatAdapter.VH>() {

    inner class VH(v: View): RecyclerView.ViewHolder(v) {
        val tv: TextView = v.findViewById(R.id.tvSeat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_seat, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = seats.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val seat = seats[position]
        holder.tv.text = "${seat.row}-${seat.col}"

        val ctx = holder.itemView.context
        val bg = holder.tv.background
        val color = when (seat.status) {
            EstadoSeat.AVAILABLE -> R.color.seat_available
            EstadoSeat.SELECTED  -> R.color.seat_selected
            EstadoSeat.SOLD      -> R.color.seat_sold
            EstadoSeat.LOCKED_OTHER -> R.color.seat_locked
        }
        bg.setTint(ContextCompat.getColor(ctx, color))
        holder.tv.background = bg

        holder.itemView.isEnabled = seat.status == EstadoSeat.AVAILABLE || seat.status == EstadoSeat.SELECTED

        holder.itemView.setOnClickListener {
            onSeatTap(seat, position)
        }
    }

    fun updateSeat(position: Int, seat: Asiento) {
        seats[position] = seat
        notifyItemChanged(position)
    }

    fun setAll(newList: List<Asiento>) {
        seats.clear()
        seats.addAll(newList)
        notifyDataSetChanged()
    }

    fun selectedSeatIds(): List<Int> = seats.filter { it.status == EstadoSeat.SELECTED }.map { it.id }
}
