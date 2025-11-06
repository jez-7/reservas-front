package com.turnos.model

data class NewServiceRequest(
    val name: String,
    val description: String,
    val duration: Int,
    val price: Double
)