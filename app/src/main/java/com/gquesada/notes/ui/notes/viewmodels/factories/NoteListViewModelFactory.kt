package com.gquesada.notes.ui.notes.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gquesada.notes.data.database.daos.NoteDao
import com.gquesada.notes.data.database.daos.TagDao
import com.gquesada.notes.domain.usecases.DeleteNoteUseCase
import com.gquesada.notes.domain.usecases.GetNotesUseCase
import com.gquesada.notes.ui.notes.viewmodels.NoteListViewModel

@Suppress("UNCHECKED_CAST")
class NoteListViewModelFactory(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        NoteListViewModel(
            getNotesUseCase = getNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
        ) as T
}