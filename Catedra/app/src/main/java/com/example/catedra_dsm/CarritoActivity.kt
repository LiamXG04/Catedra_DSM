package com.example.catedra_dsm



import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CarritoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        findViewById<TextView>(R.id.txtResumenFinal).text = """
            🎬 Película: Venom 3
            🕒 Hora: 6:30 PM
            🏛️ Sala: 3
            💺 Asientos: A1, A2, A3
            🍿 Golosinas: Combo + Refresco
            💰 Total: $25.00
        """.trimIndent()
    }
}
