package com.gquesada.notes.domain.repositories

import com.gquesada.notes.domain.models.NoteModel

interface NoteRepository {

    fun getAllNotes(): List<NoteModel>

    fun addNote(note: NoteModel)

    fun updateNote(note: NoteModel)

    fun deleteNote(id: Int)
}