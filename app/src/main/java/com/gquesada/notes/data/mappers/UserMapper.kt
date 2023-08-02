package com.gquesada.notes.data.mappers

import com.gquesada.notes.data.database.entities.UserEntity
import com.gquesada.notes.data.network.models.RemoteUser
import com.gquesada.notes.domain.models.UserModel

object UserMapper {

    fun RemoteUser.toModel(): UserModel = UserModel(
        id = id,
        firstName = firstName,
        lastName = lastName,
        token = token
    )

    fun RemoteUser.toEntity(): UserEntity = UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        token = token
    )

    fun UserEntity.toModel(): UserModel = UserModel(
        id = id,
        firstName = firstName,
        lastName = lastName,
        token = token
    )
}