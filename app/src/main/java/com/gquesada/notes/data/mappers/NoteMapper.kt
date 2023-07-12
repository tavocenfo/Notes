package com.gquesada.notes.data.mappers

import com.gquesada.notes.data.database.entities.NoteAndTag
import com.gquesada.notes.data.database.entities.NoteEntity
import com.gquesada.notes.data.mappers.TagMapper.toModel
import com.gquesada.notes.data.network.models.RemoteNote
import com.gquesada.notes.domain.models.NoteModel


object NoteMapper {

    fun NoteModel.noteEntityFromModel(): NoteEntity =
        NoteEntity(
            id = id,
            title = title,
            description = description,
            date = date,
            tagId = tag.id
        )

    fun List<NoteAndTag>.toNoteModelList(): List<NoteModel> = map { noteAndTag ->
        noteAndTag.notes.map { note ->
            NoteModel(
                id = note.id,
                title = note.title,
                description = note.description,
                date = note.date,
                tag = noteAndTag.tag.toModel()
            )
        }
    }.flatten()

    fun RemoteNote.toEntity() = NoteEntity(
        id = id.toLong(),
        title = title,
        description = description,
        date = dateCreated,
        tagId = tag.id.toLong()
    )

}