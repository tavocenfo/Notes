package com.gquesada.notes.data.mappers

import com.gquesada.notes.data.mappers.TagMapper.fromModel
import com.gquesada.notes.data.mappers.TagMapper.toModel
import com.gquesada.notes.data.models.LocalNote
import com.gquesada.notes.domain.models.NoteModel


object NoteMapper {

    fun NoteModel.noteFromModel(): LocalNote =
        LocalNote(
            id = id,
            title = title,
            description = description,
            date = date,
            tag = tag.fromModel()
        )

    fun LocalNote.toModel(): NoteModel =
        NoteModel(
            id = id,
            title = title,
            description = description,
            date = date,
            tag = tag.toModel()
        )
}