package com.gquesada.notes.data.repositories

import com.gquesada.notes.core.network.Extensions.isNotAuthorizedException
import com.gquesada.notes.data.database.entities.NoteEntity
import com.gquesada.notes.data.database.entities.TagEntity
import com.gquesada.notes.data.datasources.DatabaseNoteDataSource
import com.gquesada.notes.data.datasources.DatabaseTagDataSource
import com.gquesada.notes.data.datasources.RemoteNoteDataSource
import com.gquesada.notes.data.mappers.NoteMapper.noteEntityFromModel
import com.gquesada.notes.data.mappers.NoteMapper.toEntity
import com.gquesada.notes.data.mappers.NoteMapper.toNoteModelList
import com.gquesada.notes.data.mappers.NoteMapper.toRemote
import com.gquesada.notes.data.mappers.TagMapper.toEntity
import com.gquesada.notes.data.network.models.RemoteNote
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


class NoteRepositoryImpl(
    private val noteDataSource: DatabaseNoteDataSource,
    private val remoteNoteDataSource: RemoteNoteDataSource,
    private val tagDataSource: DatabaseTagDataSource
) : NoteRepository {

    override fun getAllNotes(): Flow<List<NoteModel>> =
        flow {
            // obtiene la data del server
            val remoteNotes = remoteNoteDataSource.getNotes()
            // LocalData es un Pair en el cual el localData.first es la lista de notas y localData.second es la lista de tags
            val localData = mapRemoteData(remoteNotes)
            // guarda la data localmente
            tagDataSource.insert(localData.second.toList())
            noteDataSource.addNotes(localData.first)
            // emite los cambios locales
            emitAll(noteDataSource.getAllNotes())
        }
            .catch {
                if (it.isNotAuthorizedException()) {
                    throw it
                } else {
                    emitAll(noteDataSource.getAllNotes())
                }
            }
            .map { it.toNoteModelList() }


    override suspend fun addNote(note: NoteModel): Long {
        remoteNoteDataSource.insert(note.toRemote())
        return noteDataSource.addNote(note.noteEntityFromModel())
    }

    override suspend fun updateNote(note: NoteModel) {
        remoteNoteDataSource.update(note.toRemote())
        noteDataSource.updateNote(note.noteEntityFromModel())
    }

    override suspend fun deleteNote(note: NoteModel) {
        remoteNoteDataSource.deleteNote(note.id.toInt())
        noteDataSource.deleteNote(note.noteEntityFromModel())
    }

    private fun mapRemoteData(data: List<RemoteNote>): Pair<List<NoteEntity>, Set<TagEntity>> {
        val notes = mutableListOf<NoteEntity>()
        val tags = mutableSetOf<TagEntity>()

        data.forEach { remote ->
            notes.add(remote.toEntity())
            tags.add(remote.tag.toEntity())
        }
        return Pair(notes, tags)
    }

}