package com.turnos.model

data class NewPersonalRequest(
    val name: String,
    val role: String,
    val email: String,
    val phone: String,
    val password: String
)