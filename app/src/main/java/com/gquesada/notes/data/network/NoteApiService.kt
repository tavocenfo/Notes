package com.gquesada.notes.data.network

import com.gquesada.notes.data.network.models.RemoteNote
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NoteApiService {

    @GET("/notes")
    suspend fun getNotes(): List<RemoteNote>

    @DELETE("/notes/{id}")
    suspend fun delete(@Path("id") noteId: Int)


    @POST("/notes")
    suspend fun insert(@Body remoteNote: RemoteNote)

    @PUT("/notes/{id}")
    suspend fun update(@Path("id") noteId: Int, @Body remoteNote: RemoteNote)
}