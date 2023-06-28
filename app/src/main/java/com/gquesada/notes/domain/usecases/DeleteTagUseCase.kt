package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository


class DeleteTagUseCase(
    private val repository: TagRepository
) {

    suspend fun execute(tag: TagModel) {
        repository.removeTag(tag)
    }
}