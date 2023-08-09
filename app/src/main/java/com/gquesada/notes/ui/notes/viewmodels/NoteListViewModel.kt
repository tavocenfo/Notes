package com.gquesada.notes.ui.notes.viewmodels

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gquesada.notes.R
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.usecases.DeleteNoteUseCase
import com.gquesada.notes.domain.usecases.DeleteNoteUseCaseOutput
import com.gquesada.notes.domain.usecases.GetNotesUseCase
import com.gquesada.notes.ui.base.BaseViewModel
import com.gquesada.notes.ui.main.viewmodels.NavigationScreen
import com.gquesada.notes.ui.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteListViewModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
) : BaseViewModel() {

    private val _noteListLiveData = MutableLiveData<List<NoteModel>>()
    val noteListLiveData: LiveData<List<NoteModel>>
        get() = _noteListLiveData

    private val _displayErrorMessage = SingleLiveEvent<Int>()
    val displayErrorMessage: LiveData<Int>
        get() = _displayErrorMessage
    private val _navigateToNote = SingleLiveEvent<NavigationScreen>()
    val navigateToNote: LiveData<NavigationScreen>
        get() = _navigateToNote

    //Referencia para entender que es un CoroutineScope y un CoroutineContext
    // actualmente no se esta utilizando
//    val context = Dispatchers.Main + SupervisorJob()
//    val scope = CoroutineScope(context)

    // cuando usamos un custom scope debemos asegurarnos de cancelar el scope en el onCleared del viewmodel
    // esto para no dejar en memoria cualquier proceso que ya no necesitemos
//    override fun onCleared() {
//        super.onCleared()
//        scope.cancel()
//    }


    fun onViewReady(arguments: Bundle?) {
        // aca puede usar el custom scope

        // scope es necesario para poder lanzar una coroutine
        viewModelScope.launch {
            getNotesUseCase.execute()
                .flowOn(Dispatchers.IO)
                .catch { handleErrorException(it) }
                .collect { notes ->
                    _noteListLiveData.value = notes
                    handleNotification(arguments)
                }
        }
    }

    fun deleteNote(note: NoteModel) {
        viewModelScope.launch {
            //lanzar la tarea
            val result = withContext(Dispatchers.IO) {
                deleteNoteUseCase.execute(note)
            }
            //luego modificar UI
            if (result is DeleteNoteUseCaseOutput.Error) {
                handleErrorException(result.exception) {
                    _displayErrorMessage.value = R.string.error_removing_note_message
                }
            }

        }
    }

    private fun handleNotification(arguments: Bundle?) {
        val noteId = arguments?.getString("note_id")?.toLongOrNull() ?: return
        val noteModel = _noteListLiveData.value?.find { note -> note.id == noteId }
        noteModel?.let { note ->
            _navigateToNote.value = NavigationScreen.EditNote(note)
        }
    }
}