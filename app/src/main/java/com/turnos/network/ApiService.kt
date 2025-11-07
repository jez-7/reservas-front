package com.turnos.network


import com.turnos.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // ---------------------------------------------------------------- ENDPOINTS DE APPOINTMENT (USADOS EN HOME) ----------------------------------------------------

    @GET("/api/appointments")
    suspend fun getAllAppointments(
        @Header("Authorization") token: String
    ): List<AppointmentDto>

    @DELETE("/api/appointments/{id}")
    suspend fun deleteAppointment(
        @Header("Authorization") token: String,
        @Path("id") appointmentId: Int
    )

    // ---------------------------------------------------------------- ENDPOINTS DE LOGIN ---------------------------------------------------------------------------

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // ---------------------------------------------------------------- ENDPOINTS DE REGISTER ---------------------------------------------------------------------------

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest)


    // ---------------------------------------------------------------- ENDPOINTS DE PROFILE ----------------------------------------------------------------------------

    // 1. OBTENER PERFIL ACTUAL
    @GET("/api/negocio/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileDto

    // 2. ACTUALIZAR DATOS DEL PERFIL
    @PUT("/api/negocio/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profileDto: ProfileDto
    )

    // 3. SUBIR IMAGEN
    @Multipart
    @POST("/api/negocio/profile/upload-image")
    suspend fun uploadProfileImage(
        @Header("Authorization") token: String,


        @Part image: MultipartBody.Part

    ): String

    // ---------------------------------------------------------------- ENDPOINTS DE SERVICES ----------------------------------------------------------------------------

    @GET("/api/service")
    suspend fun getAllServices(
        @Header("Authorization") token: String
    ): List<ServiceDto>

    @GET("/api/personal")
    suspend fun getAllPersonal(
        @Header("Authorization") token: String
    ): List<PersonalDto>

    @POST("/api/services")
    suspend fun createService(
        @Header("Authorization") token: String,
        @Body request: NewServiceRequest
    ): Unit

    @POST("/api/personal")
    suspend fun createPersonal(
        @Header("Authorization") token: String,
        @Body request: NewPersonalRequest
    ): Unit

    // ---------------------------------------------------------------- ENDPOINTS DE STATS ----------------------------------------------------------------------------

    @GET("/api/statistics")
    suspend fun getStatistics(
        @Header("Authorization") token: String
    ): StatisticsResponseDto

}