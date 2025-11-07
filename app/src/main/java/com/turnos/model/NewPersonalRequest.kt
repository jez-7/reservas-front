package com.turnos.model

import com.google.gson.annotations.SerializedName

data class NewPersonalRequest(
    @SerializedName("nombre")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("telefono")
    val phone: String,
    @SerializedName("password")
    val password: String
)