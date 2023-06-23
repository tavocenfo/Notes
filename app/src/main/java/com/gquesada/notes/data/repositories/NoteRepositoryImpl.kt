package com.gquesada.notes.data.repositories

import com.gquesada.notes.data.datasources.DatabaseNoteDataSource
import com.gquesada.notes.data.mappers.NoteMapper.localNoteFromModel
import com.gquesada.notes.data.mappers.NoteMapper.noteEntityFromModel
import com.gquesada.notes.data.mappers.NoteMapper.toModel
import com.gquesada.notes.data.mappers.NoteMapper.toNoteModelList
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class NoteRepositoryImpl(
    private val noteDataSource: DatabaseNoteDataSource,
) : NoteRepository {

    override fun getAllNotes(): Flow<List<NoteModel>> =
        noteDataSource.getAllNotes()
            .map { notes-> notes.toNoteModelList() }

    override suspend fun addNote(note: NoteModel) =
        noteDataSource.addNote(note.noteEntityFromModel())

    override suspend fun updateNote(note: NoteModel) =
        noteDataSource.updateNote(note.noteEntityFromModel())

    override suspend fun deleteNote(note: NoteModel) =
        noteDataSource.deleteNote(note.noteEntityFromModel())

}