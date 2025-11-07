package com.turnos.model

import com.google.gson.annotations.SerializedName

data class NewServiceRequest(
    @SerializedName("nombre")
    val name: String,
    @SerializedName("descripcion")
    val description: String,
    @SerializedName("duracion")
    val duration: Int,
    @SerializedName("precio")
    val price: Double
)