package com.example.catedra_dsm.model

data class Movie (
    val id: Int, // Clave única para pedir los detalles
    val title: String,
    val synopsis: String,
    val genre: String,
    val duration: Int, // En minutos
    val releaseDate: String,
    val imageUrl: String // URL del póster (servido desde Node.js)
)

data class MovieResponse(
    // Si tu API devuelve un objeto con una lista, ajústalo.
    // Ejemplo: { "movies": [...] }
    val movies: List<Movie>
)