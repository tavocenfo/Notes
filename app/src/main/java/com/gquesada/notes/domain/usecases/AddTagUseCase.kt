package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository

class AddTagUseCase(
    private val tagRepository: TagRepository
) {

    fun execute(tagModel: TagModel) {
        tagRepository.addTag(tagModel)
    }
}