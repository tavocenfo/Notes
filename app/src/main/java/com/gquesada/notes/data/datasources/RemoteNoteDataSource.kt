package com.gquesada.notes.data.datasources

import com.gquesada.notes.data.network.NoteApiService
import com.gquesada.notes.data.network.models.RemoteNote

class RemoteNoteDataSource(
    private val noteApiService: NoteApiService
) {

    suspend fun getNotes(): List<RemoteNote> {
        return noteApiService.getNotes()
    }

    suspend fun deleteNote(noteId: Int) {
        noteApiService.delete(noteId)
    }
}