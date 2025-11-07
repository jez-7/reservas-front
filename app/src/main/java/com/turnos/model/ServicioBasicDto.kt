package com.turnos.model

data class ServicioBasicDto(
    val id: Long,
    val nombre: String?,
    val descripcion: String?,
    val duracion: Int,
    val precio: Double,
    val color: String?,
    val activo: Boolean?
)