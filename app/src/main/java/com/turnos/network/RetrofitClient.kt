package com.turnos.network


import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://10.0.2.2:8081/"

object RetrofitClient {

    /**
     * Crea un cliente OkHttpClient con un interceptor de logging y el TokenManager.
     */
    private fun createHttpClient(tokenManager: TokenManager): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val token = runBlocking {
                    tokenManager.getAuthToken()
                }

                val requestBuilder = chain.request().newBuilder()

                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    /**
     * Crea la instancia de Retrofit y el servicio ApiService.
     */
    fun getApiService(tokenManager: TokenManager): ApiService {

        // ✅ Configuración de Gson con adaptadores para fechas
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
            .setLenient() // Permite JSON más flexible
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClient(tokenManager))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}