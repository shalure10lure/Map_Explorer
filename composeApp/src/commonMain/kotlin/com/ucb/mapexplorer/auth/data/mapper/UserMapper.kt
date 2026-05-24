package com.ucb.mapexplorer.auth.data.mapper

import com.ucb.mapexplorer.auth.data.dto.UserDto
import com.ucb.mapexplorer.auth.data.entity.UserEntity
import com.ucb.mapexplorer.auth.domain.model.UserModel

fun UserModel.toDto(): UserDto {
    return UserDto(
        username,
        email,
        password,
        description,
        photoUrl
    )
}
fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        email = email,
        username = username,
        description = description
    )
}