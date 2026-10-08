package com.tracktruck.app.core.data.remote.login

import com.tracktruck.app.core.domain.LoginResponse

data class LoginResponseDto(
    val id: Int,
    val username: String,
    val token: String
)

