package com.turnos.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(

    @SerializedName("email")
    val email: String,

    @SerializedName("nombre")
    val name: String,

    @SerializedName("password")
    val password: String
)