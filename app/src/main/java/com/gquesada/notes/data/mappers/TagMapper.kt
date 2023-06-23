package com.gquesada.notes.data.mappers

import android.nfc.Tag
import com.gquesada.notes.data.database.entities.TagEntity
import com.gquesada.notes.data.models.LocalTag
import com.gquesada.notes.domain.models.TagModel

object TagMapper {

    fun TagModel.fromModel(): LocalTag =
        LocalTag(
            id = id,
            title = title
        )

    fun LocalTag.toModel(): TagModel =
        TagModel(
            id = id,
            title = title
        )

    fun TagEntity.toModel(): TagModel = TagModel(
        id = id,
        title = name
    )
}