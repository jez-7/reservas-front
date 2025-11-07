package com.turnos.model

import com.google.gson.annotations.SerializedName

data class ServiceDto(


    val id: Long,
    @SerializedName("nombre")
    val name: String?,
    @SerializedName("duracion")
    val durationMinutes: Int,
    @SerializedName("descripcion")
    val description: String?,
    @SerializedName("precio")
    val price: Double,

    )