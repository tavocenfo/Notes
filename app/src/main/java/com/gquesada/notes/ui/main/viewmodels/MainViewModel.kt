package com.gquesada.notes.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationScreen>()
    val navigationEvent: LiveData<NavigationScreen>
        get() = _navigationEvent

    fun navigateTo(screen: NavigationScreen) {
        _navigationEvent.value = screen
    }
}

enum class NavigationScreen {
    AddNotes,
    TagList
}