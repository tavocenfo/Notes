package com.gquesada.notes.data.database.daos

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gquesada.notes.data.database.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(note: NoteEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(note: NoteEntity)

    @Delete
    abstract suspend fun delete(note: NoteEntity)

    @Query("SELECT * FROM notes")
    abstract fun getNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id == :id")
    abstract fun getNote(id: Long): Flow<NoteEntity>
}