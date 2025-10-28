package com.example.catedra_dsm.controller

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.core.content.edit

object TokenStore {
    private const val KEY = "jwt_token"
    fun save(context: Context, token: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit() { putString(KEY, token) }
    }
    fun get(context: Context): String? =
        PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, null)
}

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = TokenStore.get(context)
        val original: Request = chain.request()
        val builder = original.newBuilder()
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }
        return chain.proceed(builder.build())
    }
}

object RetrofitClient {
    // Base URL: emulador -> 10.0.2.2, físico -> IP de tu PC
    private const val BASE_URL = "http://10.0.2.2:3000"

    fun instance(context: Context): ApiService {
        val log = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(log)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
