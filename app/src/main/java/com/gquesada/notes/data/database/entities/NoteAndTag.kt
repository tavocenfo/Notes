package com.gquesada.notes.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class NoteAndTag(
    @Embedded val tag: TagEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tagId"
    )
    val note: List<NoteEntity>
)