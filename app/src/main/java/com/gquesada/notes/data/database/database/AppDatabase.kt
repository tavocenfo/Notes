package com.gquesada.notes.data.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gquesada.notes.data.database.daos.NoteDao
import com.gquesada.notes.data.database.daos.TagDao
import com.gquesada.notes.data.database.entities.NoteEntity
import com.gquesada.notes.data.database.entities.TagEntity

@Database(entities = [NoteEntity::class, TagEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getNotesDao(): NoteDao
    abstract fun getTagDao(): TagDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}