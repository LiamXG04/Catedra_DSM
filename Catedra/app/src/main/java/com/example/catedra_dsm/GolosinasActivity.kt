package com.example.catedra_dsm



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GolosinasActivity : AppCompatActivity() {

    private var total = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_golosinas)

        val txtSubtotal = findViewById<TextView>(R.id.txtSubtotal)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)

        total = 15 // ejemplo
        txtSubtotal.text = "Subtotal: $$total.00"

        btnContinuar.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
    }
}
