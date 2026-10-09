package com.tracktruck.app.core.domain

data class LoginResponse(
    val id: Int,
    val username: String,
    val token: String
)