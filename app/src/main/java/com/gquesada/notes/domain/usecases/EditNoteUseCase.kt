package com.gquesada.notes.domain.usecases

import com.gquesada.notes.core.network.Extensions.isNotAuthorizedException
import com.gquesada.notes.domain.exceptions.NetworkErrorException
import com.gquesada.notes.domain.exceptions.TagNullException
import com.gquesada.notes.domain.exceptions.TitleEmptyException
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.NoteRepository

class EditNoteUseCase(
    private val repository: NoteRepository
) {

    suspend fun execute(input: EditNoteUseCaseInput): EditNoteUseCaseOutput {
        return input.tagModel?.let { tag ->
            // Bloque de codigo que se ejecuta si el tag != null
            try {
                if (input.title.isEmpty()) {
                    EditNoteUseCaseOutput.Error(TitleEmptyException)
                } else {
                    val note = NoteModel(
                        id = input.noteId,
                        title = input.title,
                        description = input.description,
                        tag = tag,
                        date = input.date
                    )
                    repository.updateNote(note)
                    EditNoteUseCaseOutput.Success
                }
            } catch (e: Exception) {
                if (e.isNotAuthorizedException()) {
                    EditNoteUseCaseOutput.Error(e)
                } else {
                    EditNoteUseCaseOutput.Error(NetworkErrorException)
                }
            }
        } ?: EditNoteUseCaseOutput.Error(TagNullException)
    }
}

data class EditNoteUseCaseInput(
    val noteId: Long,
    val title: String,
    val description: String,
    val tagModel: TagModel?,
    val date: Long
)

sealed class EditNoteUseCaseOutput {
    object Success : EditNoteUseCaseOutput()
    class Error(val cause: Exception) : EditNoteUseCaseOutput()
}