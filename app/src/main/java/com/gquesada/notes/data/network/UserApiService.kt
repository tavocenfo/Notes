package com.gquesada.notes.data.network

import com.gquesada.notes.data.network.models.RemoteLogin
import com.gquesada.notes.data.network.models.RemoteUser
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {

    @POST("/user/login")
    suspend fun login(@Body login: RemoteLogin): RemoteUser
}