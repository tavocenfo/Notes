package com.gquesada.notes.data.datasources

import com.gquesada.notes.data.network.TagApiService
import com.gquesada.notes.data.network.models.RemoteTag

class RemoteTagDataSource(
    private val tagApiService: TagApiService
) {

    suspend fun getTags(): List<RemoteTag> {
        return tagApiService.getTags()
    }

    suspend fun insert(remoteTag: RemoteTag) {
        return tagApiService.insert(remoteTag)
    }

    suspend fun update(remoteTag: RemoteTag) {
        tagApiService.update(remoteTag, remoteTag.id)
    }

    suspend fun delete(tagId: Int) {
        tagApiService.delete(tagId)
    }

}