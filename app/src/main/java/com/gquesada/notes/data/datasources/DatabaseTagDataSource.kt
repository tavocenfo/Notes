package com.gquesada.notes.data.datasources

import com.gquesada.notes.data.database.daos.TagDao
import com.gquesada.notes.data.database.entities.TagEntity
import kotlinx.coroutines.flow.Flow

class DatabaseTagDataSource(
    private val tagDao: TagDao
) {

    fun getTags(): Flow<List<TagEntity>> = tagDao.getTags()

    suspend fun insert(tagEntity: TagEntity): Long =
        tagDao.insert(tagEntity)

    suspend fun delete(tagEntity: TagEntity) {
        tagDao.delete(tagEntity)
    }

    suspend fun update(tagEntity: TagEntity) {
        tagDao.update(tagEntity)
    }
}