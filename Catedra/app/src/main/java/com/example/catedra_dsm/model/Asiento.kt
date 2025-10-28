package com.example.catedra_dsm.model

data class Asiento(
    val id: Int,
    val row: Int,
    val col: Int,
    var status: EstadoSeat
)