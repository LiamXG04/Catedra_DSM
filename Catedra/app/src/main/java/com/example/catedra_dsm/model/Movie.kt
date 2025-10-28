package com.example.catedra_dsm.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,

    @SerializedName("title")
    val title: String?,

    @SerializedName("poster_url")
    val imageUrl: String?,

    @SerializedName("duration_min")
    val duration: Int?,

    @SerializedName("rating")
    val rating: String?,          // clasificación/edad

    @SerializedName("genres")
    val genres: String?,          // CSV: "Acción, Drama"

    @SerializedName("synopsis")
    val synopsis: String?
)


data class MoviesResponse(
    @SerializedName("movies")
    val movies: List<Movie>
)
