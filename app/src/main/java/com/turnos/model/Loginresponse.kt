package com.turnos.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") // el nombre que el backend usa para el JWT
    val token: String,

    @SerializedName("userRole")
    val userRole: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String
)