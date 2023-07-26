package com.gquesada.notes.ui.addnotes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gquesada.notes.R
import com.gquesada.notes.domain.exceptions.NetworkErrorException
import com.gquesada.notes.domain.exceptions.TagNullException
import com.gquesada.notes.domain.exceptions.TitleEmptyException
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.usecases.AddNoteUseCase
import com.gquesada.notes.domain.usecases.AddNoteUseCaseInput
import com.gquesada.notes.domain.usecases.AddNoteUseCaseOutput
import com.gquesada.notes.domain.usecases.EditNoteUseCase
import com.gquesada.notes.domain.usecases.EditNoteUseCaseInput
import com.gquesada.notes.domain.usecases.EditNoteUseCaseOutput
import com.gquesada.notes.ui.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNoteViewModel(
    private val addNoteUseCase: AddNoteUseCase,
    private val editNoteUseCase: EditNoteUseCase
) : ViewModel() {

    private val _tagAddedLiveData = MutableLiveData<TagModel>()
    val tagAddedLiveData: LiveData<TagModel>
        get() = _tagAddedLiveData

    // Utilizar MutableLiveData para aquellas propiedades cuyo valor modifica la
    // vista y debe prevalecer en memoria en caso de que la vista (Fragment / Activity)
    // se reconstruya
    private val _noteLiveData = MutableLiveData<NoteModel>()
    val noteLiveData: LiveData<NoteModel>
        get() = _noteLiveData

    // Utilizar SingleLiveEvent para aquellas propiedades que representan un Evento y
    // su valor deber ser consumido una unica vez
    private val _displayErrorMessageLiveData = SingleLiveEvent<Int>()
    val displayErrorMessageLiveData: LiveData<Int>
        get() = _displayErrorMessageLiveData
    private val _noteAddedLiveData = SingleLiveEvent<Unit>()
    val noteAddedLiveData: LiveData<Unit>
        get() = _noteAddedLiveData
    private val _noteUpdatedLiveData = SingleLiveEvent<Unit>()
    val noteUpdatedLiveData: LiveData<Unit>
        get() = _noteUpdatedLiveData

    private val _screenTitleLiveData = MutableLiveData(R.string.add_note_action)
    val screenTitleLiveData: LiveData<Int>
        get() = _screenTitleLiveData


    fun setNoteModel(noteModel: NoteModel?) {
        noteModel?.let { note ->
            _tagAddedLiveData.value = note.tag
            _noteLiveData.value = note
            _screenTitleLiveData.value = R.string.edit_note_screen_title
        }

    }

    fun setTag(newTagModel: TagModel) {
        _tagAddedLiveData.value = newTagModel
    }

    fun addNote(title: String, description: String) {
        val tagModel = _tagAddedLiveData.value
        val noteModel = _noteLiveData.value
        noteModel?.let {
            // bloque de codigo que se ejecuta cuando el notemodel es != null
            updateNote(it.id, it.date, title, description, tagModel)
        } ?: run {
            // bloque de codigo que se ejecuta cuando el notemodel es == null
            insertNote(title, description, tagModel)
        }
    }

    private fun insertNote(title: String, description: String, tagModel: TagModel?) {
        viewModelScope.launch {
            val result: AddNoteUseCaseOutput = withContext(Dispatchers.IO) {
                addNoteUseCase.execute(AddNoteUseCaseInput(title, description, tagModel))
            }
            when (result) {
                is AddNoteUseCaseOutput.Success -> {
                    _noteAddedLiveData.value = Unit
                }

                is AddNoteUseCaseOutput.Error -> {
                    handleAddNoteError(result.cause)
                }
            }
        }
    }

    private fun updateNote(
        noteId: Long,
        date: Long,
        title: String,
        description: String,
        tagModel: TagModel?
    ) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                editNoteUseCase.execute(
                    EditNoteUseCaseInput(
                        noteId = noteId,
                        title = title,
                        description = description,
                        date = date,
                        tagModel = tagModel
                    )
                )
            }
            when (result) {
                is EditNoteUseCaseOutput.Success -> {
                    _noteUpdatedLiveData.value = Unit
                }

                is EditNoteUseCaseOutput.Error -> {
                    handleAddNoteError(result.cause)
                }
            }

        }
    }

    private fun handleAddNoteError(error: Exception) {
        when (error) {
            is TitleEmptyException -> {
                _displayErrorMessageLiveData.value = R.string.add_note_empty_title_error
            }

            is TagNullException -> {
                _displayErrorMessageLiveData.value = R.string.add_note_empty_tag_error
            }
            is NetworkErrorException -> {
                _displayErrorMessageLiveData.value = R.string.error_updating_note_message
            }
        }
    }
}