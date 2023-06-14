package com.gquesada.notes.data.mappers

import com.gquesada.notes.data.models.LocalTag
import com.gquesada.notes.domain.models.TagModel

object TagMapper {

    fun LocalTag.toTag(): TagModel = TagModel(
        id = this.id,
        title = this.title
    )
}