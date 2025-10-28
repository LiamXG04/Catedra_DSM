package com.example.catedra_dsm.controller
import com.example.catedra_dsm.model.Candy
import com.example.catedra_dsm.model.Movie
import com.example.catedra_dsm.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // 1. Obtiene la lista de películas para la cartelera
    @GET("api/movies") // Asegúrate que el endpoint coincida con tu API de Node.js
    suspend fun getMovies(): Response<MovieResponse>

    // 2. Obtiene los detalles de una sola película por su ID
    @GET("api/movies/{id}")
    suspend fun getMovieDetail(@Path("id") id: Int): Response<Movie>

    // 3. Obtiene el menú de golosinas
    @GET("api/candies")
    suspend fun getCandies(): Response<List<Candy>>
}