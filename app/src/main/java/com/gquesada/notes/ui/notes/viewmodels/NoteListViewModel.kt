package com.gquesada.notes.ui.notes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gquesada.notes.data.datasources.LocalNoteDataSource
import com.gquesada.notes.data.repositories.NoteRepositoryImpl
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.repositories.NoteRepository
import com.gquesada.notes.domain.usecases.DeleteNoteUseCase
import com.gquesada.notes.domain.usecases.GetNotesUseCase

class NoteListViewModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _noteListLiveData = MutableLiveData<List<NoteModel>>()
    val noteListLiveData: LiveData<List<NoteModel>>
        get() = _noteListLiveData

    fun onViewReady() {
        val list = getNotesUseCase.execute()
        _noteListLiveData.value = list
    }

    fun deleteNote(note: NoteModel) {
        val list = _noteListLiveData.value?.toMutableList() ?: return
        deleteNoteUseCase.execute(note.id)
        list.removeIf { item -> note.id == item.id }
        _noteListLiveData.value = list
    }
}