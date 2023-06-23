package com.gquesada.notes.data.datasources

import com.gquesada.notes.data.database.daos.NoteDao
import com.gquesada.notes.data.database.daos.TagDao
import com.gquesada.notes.data.database.entities.NoteAndTag
import com.gquesada.notes.data.database.entities.NoteEntity
import com.gquesada.notes.data.models.LocalNote
import kotlinx.coroutines.flow.Flow

class DatabaseNoteDataSource(
    private val tagDao: TagDao,
    private val noteDao: NoteDao
) {

    fun getAllNotes(): Flow<List<NoteAndTag>> = tagDao.getTagsAndNotes()

    suspend fun addNote(noteEntity: NoteEntity):Long =
       noteDao.insert(noteEntity)

    suspend fun deleteNote(noteEntity: NoteEntity) {
        noteDao.delete(noteEntity)
    }

    suspend fun updateNote(noteEntity: NoteEntity) {
        noteDao.update(noteEntity)
    }
}