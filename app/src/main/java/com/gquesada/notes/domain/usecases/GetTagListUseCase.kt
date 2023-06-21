package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository


class GetTagListUseCase(
    private val tagRepository: TagRepository
) {

    fun execute(): List<TagModel> = tagRepository.getTags()
}