package com.turnos.model

import com.google.gson.annotations.SerializedName

data class PersonalDto(
    val id: Long,
    @SerializedName("nombre")
    val name: String?,
    @SerializedName("rolNegocio")
    val role: String?,
    @SerializedName("activo")
    val isActive: Boolean,

    )