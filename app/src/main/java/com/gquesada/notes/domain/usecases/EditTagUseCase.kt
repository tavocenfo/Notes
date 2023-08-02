package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository

class EditTagUseCase(
    private val tagRepository: TagRepository
) {

    suspend fun execute(tagModel: TagModel): EditTagUseCaseOutput =
        try {
            tagRepository.editTag(tagModel)
            EditTagUseCaseOutput.Success
        } catch (e: Exception) {
            EditTagUseCaseOutput.Error(e)
        }


}

sealed class EditTagUseCaseOutput {
    object Success : EditTagUseCaseOutput()
    data class Error(val exception:Exception) : EditTagUseCaseOutput()
}