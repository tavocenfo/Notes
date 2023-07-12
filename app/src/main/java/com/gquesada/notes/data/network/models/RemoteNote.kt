package com.gquesada.notes.data.network.models

import com.squareup.moshi.Json

data class RemoteNote(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "description") val description: String? = null,
    @field:Json(name = "date_created") val dateCreated: Long,
    @field:Json(name = "tag") val tag: RemoteTag
)
