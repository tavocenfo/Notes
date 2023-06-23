package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNotesUseCase(
    private val noteRepository: NoteRepository
) {

    fun execute(): Flow<List<NoteModel>> =
        noteRepository.getAllNotes()
            .map { notes -> notes.sortedBy { note -> note.id } }

}