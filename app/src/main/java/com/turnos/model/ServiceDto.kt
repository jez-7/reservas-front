package com.turnos.model

data class ServiceDto(
    val id: Int,
    val name: String,
    val durationMinutes: Int,
    val description: String,
    val price: Double,

    )