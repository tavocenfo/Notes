package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository


class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    suspend fun execute(note: NoteModel) {
        repository.deleteNote(note)
    }
}