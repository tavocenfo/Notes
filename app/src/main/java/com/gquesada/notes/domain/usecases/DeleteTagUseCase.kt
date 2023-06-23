package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.repositories.TagRepository


class DeleteTagUseCase(
    private val repository: TagRepository
) {

    fun execute(id: Long) {
        repository.removeTag(id)
    }
}