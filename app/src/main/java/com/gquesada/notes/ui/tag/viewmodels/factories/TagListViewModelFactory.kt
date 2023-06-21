package com.gquesada.notes.ui.tag.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gquesada.notes.domain.usecases.DeleteTagUseCase
import com.gquesada.notes.domain.usecases.GetTagListUseCase
import com.gquesada.notes.ui.tag.viewmodels.TagListViewModel

@Suppress("UNCHECKED_CAST")
class TagListViewModelFactory(
    private val getTagListUseCase: GetTagListUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TagListViewModel(getTagListUseCase, deleteTagUseCase) as T
}