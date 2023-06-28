package com.gquesada.notes.domain.repositories

import com.gquesada.notes.domain.models.TagModel
import kotlinx.coroutines.flow.Flow

interface TagRepository {

    fun getTags(): Flow<List<TagModel>>
    suspend fun addTag(tag: TagModel)
    suspend fun removeTag(tag: TagModel)

    suspend fun editTag(tag: TagModel)
}