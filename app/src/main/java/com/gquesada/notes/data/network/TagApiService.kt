package com.gquesada.notes.data.network

import com.gquesada.notes.data.network.models.RemoteTag
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TagApiService {

    @GET("/tags")
    suspend fun getTags(): List<RemoteTag>

    @DELETE("/tags/{id}")
    suspend fun delete(@Path("id") tagId: Int)

    @POST("/tags")
    suspend fun insert(@Body remoteTag: RemoteTag)

    @PUT("/tags/{id}")
    suspend fun update(@Body remoteTag: RemoteTag, @Path("id") id:Int)
}