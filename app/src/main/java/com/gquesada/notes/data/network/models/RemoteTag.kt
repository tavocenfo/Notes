package com.gquesada.notes.data.network.models

import com.squareup.moshi.Json

data class RemoteTag(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String
)