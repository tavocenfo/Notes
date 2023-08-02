package com.gquesada.notes.data.datasources

import com.gquesada.notes.data.network.UserApiService
import com.gquesada.notes.data.network.models.RemoteLogin
import com.gquesada.notes.data.network.models.RemoteUser

class RemoteUserDataSource(
    private val apiService: UserApiService
) {

    suspend fun login(email: String, password: String): RemoteUser =
        apiService.login(RemoteLogin(email, password))
}