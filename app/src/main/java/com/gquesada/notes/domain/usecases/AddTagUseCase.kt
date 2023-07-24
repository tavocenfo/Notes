package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository

class AddTagUseCase(
    private val tagRepository: TagRepository
) {
    suspend fun execute(tagModel: TagModel): AddTagUseCaseOutput =
        try {
            tagRepository.addTag(tagModel)
            AddTagUseCaseOutput.Success
        } catch (e: Exception) {
            AddTagUseCaseOutput.Error
        }

}

sealed class AddTagUseCaseOutput {
    object Success : AddTagUseCaseOutput()
    object Error : AddTagUseCaseOutput()
}