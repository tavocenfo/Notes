package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository


class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    suspend fun execute(note: NoteModel): DeleteNoteUseCaseOutput =
        try {
            repository.deleteNote(note)
            DeleteNoteUseCaseOutput.Success
        } catch (e: Exception) {
            DeleteNoteUseCaseOutput.Error(e)
        }

}

sealed class DeleteNoteUseCaseOutput {
    object Success : DeleteNoteUseCaseOutput()
    data class Error(val exception: Exception) : DeleteNoteUseCaseOutput()
}