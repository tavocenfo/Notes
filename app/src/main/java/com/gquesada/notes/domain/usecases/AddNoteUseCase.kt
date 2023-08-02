package com.gquesada.notes.domain.usecases

import com.gquesada.notes.core.network.Extensions.isNotAuthorizedException
import com.gquesada.notes.domain.exceptions.NetworkErrorException
import com.gquesada.notes.domain.exceptions.TagNullException
import com.gquesada.notes.domain.exceptions.TitleEmptyException
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.NoteRepository

class AddNoteUseCase(
    private val repository: NoteRepository
) {

    suspend fun execute(input: AddNoteUseCaseInput): AddNoteUseCaseOutput {
        return input.tag?.let { tag ->
            try {
                if (input.title.isEmpty()) {
                    AddNoteUseCaseOutput.Error(TitleEmptyException)
                } else {
                    val note = NoteModel(
                        id = 0,
                        title = input.title,
                        description = input.description,
                        tag = tag,
                        date = System.currentTimeMillis()
                    )
                    repository.addNote(note)
                    AddNoteUseCaseOutput.Success
                }
            } catch (e: Exception) {
                if (e.isNotAuthorizedException()) {
                    AddNoteUseCaseOutput.Error(e)
                } else {
                    AddNoteUseCaseOutput.Error(NetworkErrorException)
                }
            }

        } ?: AddNoteUseCaseOutput.Error(TagNullException)
    }
}

data class AddNoteUseCaseInput(
    val title: String,
    val description: String,
    val tag: TagModel?
)

sealed class AddNoteUseCaseOutput {
    object Success : AddNoteUseCaseOutput()
    class Error(val cause: Exception) : AddNoteUseCaseOutput()
}
