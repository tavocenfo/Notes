package com.gquesada.notes.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gquesada.notes.data.database.entities.NoteAndTag
import com.gquesada.notes.data.database.entities.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(tagEntity: TagEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(tagEntity: TagEntity)

    @Delete
    abstract suspend fun delete(tagEntity: TagEntity)

    @Query("SELECT * FROM tags, notes WHERE tags.id == notes.tagId")
    abstract fun getTagsAndNotes(): Flow<List<NoteAndTag>>

    @Query("SELECT * FROM tags")
    abstract fun getTags(): Flow<List<TagEntity>>
}