package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository

class GetNotesUseCase(
    private val noteRepository: NoteRepository
) {

    fun execute(): List<NoteModel> = noteRepository.getAllNotes()
}