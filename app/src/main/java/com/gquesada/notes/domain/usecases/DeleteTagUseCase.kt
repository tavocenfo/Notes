package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository


class DeleteTagUseCase(
    private val repository: TagRepository
) {

    suspend fun execute(tag: TagModel): DeleteTagUseCaseOutput =
        try {
            repository.removeTag(tag)
            DeleteTagUseCaseOutput.Success
        } catch (e: Exception) {
            DeleteTagUseCaseOutput.Error(e)
        }
}

sealed class DeleteTagUseCaseOutput {
    object Success : DeleteTagUseCaseOutput()
    data class Error(val exception: Exception) : DeleteTagUseCaseOutput()
}