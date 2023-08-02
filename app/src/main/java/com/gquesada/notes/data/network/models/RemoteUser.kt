package com.gquesada.notes.data.network.models

import com.squareup.moshi.Json

data class RemoteUser(
    @field:Json(name = "_id") val id: String,
    @field:Json(name = "first_name") val firstName: String,
    @field:Json(name = "last_name") val lastName: String,
    @field:Json(name = "token") val token: String
)
