package com.gquesada.notes.domain.repositories

import com.gquesada.notes.domain.models.NoteModel
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getAllNotes(): Flow<List<NoteModel>>

    suspend fun addNote(note: NoteModel): Long

    suspend fun updateNote(note: NoteModel)

    suspend fun deleteNote(note: NoteModel)
}