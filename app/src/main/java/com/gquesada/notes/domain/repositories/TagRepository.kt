package com.gquesada.notes.domain.repositories

import com.gquesada.notes.domain.models.TagModel

interface TagRepository {

    fun getTags(): List<TagModel>
    fun addTag(tag: TagModel)
    fun removeTag(id: Int)
}