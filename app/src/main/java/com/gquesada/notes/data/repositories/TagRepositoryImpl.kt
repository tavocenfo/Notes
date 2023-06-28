package com.gquesada.notes.data.repositories

import com.gquesada.notes.data.datasources.DatabaseTagDataSource
import com.gquesada.notes.data.datasources.LocalTagDataSource
import com.gquesada.notes.data.mappers.TagMapper.fromModel
import com.gquesada.notes.data.mappers.TagMapper.toEntity
import com.gquesada.notes.data.mappers.TagMapper.toModel
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TagRepositoryImpl(
    private val databaseTagDataSource: DatabaseTagDataSource
) : TagRepository {

    override fun getTags(): Flow<List<TagModel>> =
        databaseTagDataSource.getTags().map { items -> items.map { tag -> tag.toModel() } }

    override suspend fun addTag(tag: TagModel) {
        databaseTagDataSource.insert(tag.toEntity())
    }

    override suspend fun removeTag(tag: TagModel) {
        databaseTagDataSource.delete(tag.toEntity())
    }

    override suspend fun editTag(tag: TagModel) {
        databaseTagDataSource.update(tag.toEntity())
    }
}