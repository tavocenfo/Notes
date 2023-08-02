package com.gquesada.notes.data.repositories

import com.gquesada.notes.data.datasources.DatabaseUserDataSource
import com.gquesada.notes.data.datasources.RemoteUserDataSource
import com.gquesada.notes.data.mappers.UserMapper.toEntity
import com.gquesada.notes.data.mappers.UserMapper.toModel
import com.gquesada.notes.domain.models.UserModel
import com.gquesada.notes.domain.repositories.UserRepository

class UserRepositoryImpl(
    private val remoteUserDataSource: RemoteUserDataSource,
    private val databaseUserDataSource: DatabaseUserDataSource
) : UserRepository {

    private var token: String? = null

    override suspend fun login(email: String, password: String): UserModel {
        val userRemote = remoteUserDataSource.login(email, password)
        databaseUserDataSource.insert(userRemote.toEntity())
        token = userRemote.token
        return userRemote.toModel()
    }

    override suspend fun fetchUserToken(): String? {
        return token ?: databaseUserDataSource.getUser()?.token
    }

    override suspend fun getUser(): UserModel? =
        databaseUserDataSource.getUser()?.toModel()

    override suspend fun logout() {
        token = null
        databaseUserDataSource.deleteAll()
    }
}