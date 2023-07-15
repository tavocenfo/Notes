package com.gquesada.notes.data.repositories

import com.gquesada.notes.data.datasources.LocalNoteDataSource
import com.gquesada.notes.data.mappers.NoteMapper.noteFromModel
import com.gquesada.notes.data.mappers.NoteMapper.toModel
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository


class NoteRepositoryImpl(
    private val noteDataSource: LocalNoteDataSource
) : NoteRepository {

    override fun getAllNotes(): List<NoteModel> =
        noteDataSource.getAllNotes()
            .map { it.toModel() }

    override fun addNote(note: NoteModel) =
        noteDataSource.addNote(note.noteFromModel())

    override fun updateNote(note: NoteModel) =
        noteDataSource.updateNote(note.noteFromModel())

    override fun deleteNote(id: Int) =
        noteDataSource.deleteNote(id)

}