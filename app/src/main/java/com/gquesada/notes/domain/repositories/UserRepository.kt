package com.gquesada.notes.domain.repositories

import com.gquesada.notes.domain.models.UserModel

interface UserRepository {

    suspend fun login(email: String, password: String): UserModel

    suspend fun fetchUserToken(): String?

    suspend fun getUser(): UserModel?

    suspend fun logout()
}