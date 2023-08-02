package com.gquesada.notes.data.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gquesada.notes.data.database.daos.NoteDao
import com.gquesada.notes.data.database.daos.TagDao
import com.gquesada.notes.data.database.daos.UserDao
import com.gquesada.notes.data.database.entities.NoteEntity
import com.gquesada.notes.data.database.entities.TagEntity
import com.gquesada.notes.data.database.entities.UserEntity

@Database(entities = [NoteEntity::class, TagEntity::class, UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getNotesDao(): NoteDao
    abstract fun getTagDao(): TagDao

    abstract fun getUserDao(): UserDao

}