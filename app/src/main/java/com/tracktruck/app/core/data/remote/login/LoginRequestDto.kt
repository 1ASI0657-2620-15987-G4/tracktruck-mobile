package com.tracktruck.app.core.data.remote.login

import com.tracktruck.app.core.domain.LoginRequest

data class LoginRequestDto(
    val username: String,
    val password: String
)

