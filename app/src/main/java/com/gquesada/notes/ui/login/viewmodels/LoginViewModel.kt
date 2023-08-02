package com.gquesada.notes.ui.login.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gquesada.notes.R
import com.gquesada.notes.domain.usecases.LoginUseCase
import com.gquesada.notes.domain.usecases.LoginUseCaseInput
import com.gquesada.notes.domain.usecases.LoginUseCaseOutput
import com.gquesada.notes.ui.main.viewmodels.NavigationScreen
import com.gquesada.notes.ui.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _isSignInActionEnabled = MutableLiveData(false)
    val isSignInActionEnabled: LiveData<Boolean>
        get() = _isSignInActionEnabled
    private val _displayLoadingProgressBar = MutableLiveData(View.GONE)
    val displayLoadingProgressBar: LiveData<Int>
        get() = _displayLoadingProgressBar
    private val _navigateToInitialScreenEvent = SingleLiveEvent<NavigationScreen>()
    val navigateToInitialScreenEvent: LiveData<NavigationScreen>
        get() = _navigateToInitialScreenEvent
    private val _displayLoginError = SingleLiveEvent<Int>()
    val displayLoginError: LiveData<Int>
        get() = _displayLoginError

    private var email = ""
    private var password = ""

    fun onEmailUpdated(email: String) {
        this.email = email
        shouldButtonBeEnabled()
    }

    fun onPasswordUpdated(password: String) {
        this.password = password
        shouldButtonBeEnabled()
    }

    private fun shouldButtonBeEnabled() {
        _isSignInActionEnabled.value = email.isNotEmpty() && password.isNotEmpty()
    }

    fun onLogin() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                loginUseCase.execute(LoginUseCaseInput(email, password))
            }
            handleLoginResult(result)
        }
    }

    private fun handleLoginResult(result: LoginUseCaseOutput) {
        when (result) {
            is LoginUseCaseOutput.Success -> _navigateToInitialScreenEvent.value =
                NavigationScreen.NoteList

            is LoginUseCaseOutput.Error -> handleLoginError(result)
        }
    }

    private fun handleLoginError(result: LoginUseCaseOutput.Error) {
        when (result) {
            is LoginUseCaseOutput.Error.EmptyFields ->
                _displayLoginError.value = R.string.login_empty_fields_error

            is LoginUseCaseOutput.Error.NetworkError ->
                _displayLoginError.value = R.string.login_network_error

            is LoginUseCaseOutput.Error.InvalidCredentials ->
                _displayLoginError.value = R.string.login_invalid_credentials_error
        }
    }
}