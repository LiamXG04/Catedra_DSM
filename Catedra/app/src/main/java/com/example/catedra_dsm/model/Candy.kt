// com/example/catedra_dsm/model/Candy.kt
package com.example.catedra_dsm.model

import com.google.gson.annotations.SerializedName

data class Candy(
    val id: Int,
    @SerializedName("nombre") val name: String,
    @SerializedName("precio") val price: Double,
    @SerializedName("stock") val stock: Int? = null,
    @SerializedName("imagen_url") val imageUrl: String? = null
)
