package com.example.catedra_dsm.controller

import com.example.catedra_dsm.model.Asiento
import com.example.catedra_dsm.model.Movie
import com.example.catedra_dsm.model.MoviesResponse
import com.example.catedra_dsm.model.Candy
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Películas
    @GET("/api/movies")
    suspend fun getMovies(): Response<List<Movie>> // usa MoviesResponse si tu API envuelve

    @GET("/api/movies/{id}")
    suspend fun getMovie(@Path("id") id: Int): Response<Movie>

    // Auth
    data class LoginReq(val email: String, val password: String)
    data class LoginRes(val token: String)
    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginReq): Response<LoginRes>

    // Showtimes & seats (ejemplos)
    @GET("/api/showtimes")
    suspend fun getShowtimes(): Response<Any>

    @GET("/api/seats")
    suspend fun getSeats(@Query("showtimeId") showtimeId: Int): Response<Any>

    // Locks (bloqueos) – si implementaste /api/locks
    data class LockReq(val showtimeId: Int, val seatIds: List<Int>)
    @POST("/api/locks")
    suspend fun lockSeats(@Body req: LockReq): Response<Any>

    @DELETE("/api/locks/{showtimeId}")
    suspend fun releaseLocks(@Path("showtimeId") showtimeId: Int): Response<Any>

    // Orders
    data class OrderReq(
        val showtimeId: Int,
        val seatIds: List<Int>,
        val detalle: List<SnackItem>?
    )
    data class SnackItem(val id_golosina: Int, val cantidad: Int)
    @POST("/api/orders")
    suspend fun createOrder(@Body req: OrderReq): Response<Any>

    @GET("/api/golosinas")
    suspend fun getCandies(): Response<List<Candy>>


    @GET("/api/orders/mine")
    suspend fun myOrders(): Response<Any>

    @GET("/api/showtimes/{id}/seats")
    suspend fun getAsiento(@Path("id") showtimeId: Int): Response<List<Asiento>>

    @POST("/api/locks")
    suspend fun lockSeat(@Body body: Map<String, Any>): Response<Unit>

    @DELETE("/api/locks/{showtimeId}/{seatId}")
    suspend fun unlockSeat(@Path("showtimeId") showtimeId: Int, @Path("seatId") seatId: Int): Response<Unit>
}
