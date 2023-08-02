package com.gquesada.notes.data.repositories

import com.gquesada.notes.core.network.Extensions.isNotAuthorizedException
import com.gquesada.notes.data.datasources.DatabaseTagDataSource
import com.gquesada.notes.data.datasources.RemoteTagDataSource
import com.gquesada.notes.data.mappers.TagMapper.toEntity
import com.gquesada.notes.data.mappers.TagMapper.toModel
import com.gquesada.notes.data.mappers.TagMapper.toRemote
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


class TagRepositoryImpl(
    private val databaseTagDataSource: DatabaseTagDataSource,
    private val remoteTagDataSource: RemoteTagDataSource
) : TagRepository {

    override fun getTags(): Flow<List<TagModel>> {
        return flow {
            val tags = remoteTagDataSource.getTags()
                .map { it.toEntity() }
            databaseTagDataSource.insert(tags)
            emitAll(databaseTagDataSource.getTags())
        }.catch {
            if (it.isNotAuthorizedException()) {
                throw it
            }
            emitAll(databaseTagDataSource.getTags())
        }
            .map { items -> items.map { tag -> tag.toModel() } }
    }


    override suspend fun addTag(tag: TagModel) {
        remoteTagDataSource.insert(tag.toRemote())
        databaseTagDataSource.insert(tag.toEntity())
    }

    override suspend fun removeTag(tag: TagModel) {
        remoteTagDataSource.delete(tag.id.toInt())
        databaseTagDataSource.delete(tag.toEntity())
    }

    override suspend fun editTag(tag: TagModel) {
        remoteTagDataSource.update(tag.toRemote())
        databaseTagDataSource.update(tag.toEntity())
    }
}