package com.tracktruck.app.core.data.remote.user

import com.tracktruck.app.core.domain.User

data class UserDto(
    val id: Int,
    val username: String,
    val phone: String,
    val state: Boolean,
    val modifiedAt: String
)

data class UserRoleDto(val role: Boolean)

data class UserStateUpdateDto(val state: Boolean)

fun UserDto.toUser() = User(
    id = id,
    username = username,
    phone = phone,
    state = state,
    modifiedAt = modifiedAt
)
