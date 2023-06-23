package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository

class AddNoteUseCase(
    private val repository: NoteRepository
) {

    suspend fun execute(noteModel: NoteModel) {
        repository.addNote(noteModel)
    }
}