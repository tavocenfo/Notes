package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository

class EditTagUseCase(
    private val tagRepository: TagRepository
) {

    fun execute(tagModel: TagModel) {
        tagRepository.editTag(tagModel)
    }
}