package com.gquesada.notes.data.repositories

import com.gquesada.notes.data.datasources.LocalTagDataSource
import com.gquesada.notes.data.mappers.TagMapper.fromModel
import com.gquesada.notes.data.mappers.TagMapper.toModel
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository


class TagRepositoryImpl(
    private val tagDataSource: LocalTagDataSource
) : TagRepository {

    override fun getTags(): List<TagModel> =
        tagDataSource.getTags().map { it.toModel() }

    override fun addTag(tag: TagModel) {
        tagDataSource.addTag(tag.fromModel())
    }

    override fun removeTag(id: Int) {
        tagDataSource.removeTag(id)
    }
}