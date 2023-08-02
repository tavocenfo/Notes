package com.gquesada.notes.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gquesada.notes.core.network.Extensions.isNotAuthorizedException
import com.gquesada.notes.ui.util.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {

    private val _hasSessionEnded = SingleLiveEvent<Unit>()
    val hasSessionEnded: LiveData<Unit>
        get() = _hasSessionEnded


    protected fun handleErrorException(exception: Throwable, onError: (() -> Unit)? = null) {
        if (exception.isNotAuthorizedException()) {
            _hasSessionEnded.call()
        } else {
            onError?.invoke()
        }
    }

}

