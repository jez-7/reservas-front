package com.turnos.model

data class ClienteBasicDto(
    val id: Long,
    val nombre: String?,
    val telefono: String?,
    val email: String?
)