package com.gquesada.notes.ui.main.viewmodels

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.usecases.GetCurrentUserUseCase
import com.gquesada.notes.domain.usecases.LogoutUseCase
import com.gquesada.notes.ui.util.SingleLiveEvent
import kotlinx.coroutines.launch

class MainViewModel(
    private val useCase: GetCurrentUserUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    private val _navigationEvent = SingleLiveEvent<NavigationScreen>()
    val navigationEvent: LiveData<NavigationScreen>
        get() = _navigationEvent

    val shouldDrawerBeEnabled = _navigationEvent.map { it !is NavigationScreen.Login }

    fun onAppOpened(bundle: Bundle?) {
        //change this with user session
        // if user has a session, open NoteList
        viewModelScope.launch {
            useCase.execute()?.let {
                navigateTo(NavigationScreen.NoteList(bundle))
            } ?: navigateTo(NavigationScreen.Login(bundle))
        }
        // if not, open Login
    }

    fun navigateTo(screen: NavigationScreen) {
        _navigationEvent.value = screen
    }

    fun onLogOut() {
        // implement logout event
        viewModelScope.launch {
            logout.execute()
            navigateTo(NavigationScreen.Login())
        }

    }
}

sealed class NavigationScreen(val isInitialScreen: Boolean, val arguments: Bundle? = null) {

    data class Login(private val bundle: Bundle? = null) : NavigationScreen(true, bundle)

    data class NoteList(private val bundle: Bundle? = null) : NavigationScreen(true, bundle)
    object AddNotes : NavigationScreen(false)

    object TagList : NavigationScreen(false)
    data class EditNote(val note: NoteModel) : NavigationScreen(false)
}