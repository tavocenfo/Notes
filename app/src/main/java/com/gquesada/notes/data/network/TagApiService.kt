package com.gquesada.notes.data.network

import com.gquesada.notes.data.network.models.RemoteTag
import retrofit2.http.GET

interface TagApiService {

    @GET("/tags")
    suspend fun getTags(): List<RemoteTag>
}