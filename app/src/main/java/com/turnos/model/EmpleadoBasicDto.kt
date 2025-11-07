package com.turnos.model

import com.google.gson.annotations.SerializedName

data class EmpleadoBasicDto(
    val id: Long,
    @SerializedName("nombreUsuario")
    val nombreUsuario: String?,
    @SerializedName("rolNegocio")
    val rolNegocio: String?,
    val activo: Boolean?
)