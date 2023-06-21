package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.repositories.NoteRepository


class DeleteNoteUseCase(
    private val repository: NoteRepository
) {

    fun execute(id: Int) {
        repository.deleteNote(id)
    }
}