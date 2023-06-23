package com.gquesada.notes.data.mappers

import com.gquesada.notes.data.database.entities.NoteAndTag
import com.gquesada.notes.data.database.entities.NoteEntity
import com.gquesada.notes.data.mappers.TagMapper.fromModel
import com.gquesada.notes.data.mappers.TagMapper.toModel
import com.gquesada.notes.data.models.LocalNote
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.models.TagModel


object NoteMapper {

    fun NoteModel.localNoteFromModel(): LocalNote =
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

    fun test(list: List<NoteAndTag>): List<NoteModel> {
        val notes = mutableListOf<NoteModel>()
        list.forEach { noteAndTag ->
            noteAndTag.notes.forEach { note ->
                notes.add(
                    NoteModel(
                        id = note.id,
                        title = note.title,
                        description = note.description,
                        date = note.date,
                        tag = noteAndTag.tag.toModel()
                    )
                )
            }
        }
        return notes
    }

}