package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository
import kotlinx.coroutines.flow.Flow


class GetTagListUseCase(
    private val tagRepository: TagRepository
) {

    fun execute(): Flow<List<TagModel>> = tagRepository.getTags()
}