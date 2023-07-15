package com.gquesada.notes.ui.addnotes.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gquesada.notes.domain.usecases.AddNoteUseCase
import com.gquesada.notes.ui.addnotes.viewmodels.AddNoteViewModel

@Suppress("UNCHECKED_CAST")
class AddNoteViewModelFactory(
    private val useCase: AddNoteUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AddNoteViewModel(useCase) as T

}