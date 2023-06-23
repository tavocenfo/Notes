package com.gquesada.notes.ui.addnotes.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gquesada.notes.R
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.usecases.AddNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNoteViewModel(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    private val _tagAddedLiveData = MutableLiveData<TagModel>()
    val tagAddedLiveData: LiveData<TagModel>
        get() = _tagAddedLiveData

    private val _displayErrorMessageLiveData = MutableLiveData<Int>()
    val displayErrorMessageLiveData: LiveData<Int>
        get() = _displayErrorMessageLiveData
    private val _noteAddedLiveData = MutableLiveData<Unit>()
    val noteAddedLiveData: LiveData<Unit>
        get() = _noteAddedLiveData


    fun setTag(newTagModel: TagModel) {
        _tagAddedLiveData.value = newTagModel
    }

    fun addNote(title: String, description: String) {
        val tagModel = _tagAddedLiveData.value
        viewModelScope.launch {
            tagModel?.let { tag ->
                if (title.isEmpty()) {
                    _displayErrorMessageLiveData.value = R.string.add_note_empty_title_error
                } else {
                    val note = NoteModel(
                        id = 0,
                        title = title,
                        description = description,
                        tag = tag,
                        date = System.currentTimeMillis()
                    )
                    withContext(Dispatchers.IO) {
                        addNoteUseCase.execute(note)
                    }
                    _noteAddedLiveData.value = Unit
                }
            } ?: run {
                _displayErrorMessageLiveData.value = R.string.add_note_empty_title_error
            }
        }
    }
}