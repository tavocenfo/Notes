package com.gquesada.notes.data.mappers

import com.gquesada.notes.data.database.entities.TagEntity
import com.gquesada.notes.data.network.models.RemoteTag
import com.gquesada.notes.domain.models.TagModel

object TagMapper {

    fun TagEntity.toModel(): TagModel = TagModel(
        id = id,
        title = name
    )

    fun TagModel.toEntity():TagEntity = TagEntity(
        id = id,
        name = title
    )

    fun RemoteTag.toEntity():TagEntity = TagEntity(
        id = id.toLong(),
        name = name
    )
}