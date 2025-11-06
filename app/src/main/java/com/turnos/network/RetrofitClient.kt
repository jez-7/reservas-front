package com.turnos.network

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



private const val BASE_URL = "http://10.0.2.2:8080/"


object RetrofitClient {

    /**
     * Crea un cliente OkHttpClient con un interceptor de logging y el TokenManager.
     */
    private fun createHttpClient(tokenManager: TokenManager): OkHttpClient {


        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // muestra el cuerpo del request/response
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            // añade el token en la cabecera "Authorization"
            .addInterceptor { chain ->

                val token = runBlocking {
                    tokenManager.getAuthToken()
                }

                val requestBuilder = chain.request().newBuilder()

                if (!token.isNullOrBlank()) {
                    // 2. Añadir el encabezado de autorización
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

        // Configuración para manejar los objetos JSON
        val gson = GsonBuilder()
            // .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // Si manejas Date/Time
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClient(tokenManager)) // Usa el cliente OkHttp personalizado
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}