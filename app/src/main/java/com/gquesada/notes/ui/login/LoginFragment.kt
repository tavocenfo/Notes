package com.gquesada.notes.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.gquesada.notes.R
import com.gquesada.notes.ui.login.viewmodels.LoginViewModel
import com.gquesada.notes.ui.main.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModel()

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        initViews(view)
        observe()
        return view
    }

    private fun initViews(view: View) {
        with(view) {
            edtEmail = findViewById(R.id.username)
            setEmailListener()
            edtPassword = findViewById(R.id.password)
            setPasswordListener()
            btnSignIn = findViewById(R.id.login)
            setLoginListener()
            loadingProgressBar = findViewById(R.id.loading)
        }
    }

    private fun setEmailListener() {
        edtEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailUpdated(text.toString())
        }
    }

    private fun setPasswordListener() {
        edtPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordUpdated(text.toString())
        }
    }

    private fun setLoginListener() {
        btnSignIn.setOnClickListener { viewModel.onLogin(arguments) }
    }

    private fun observe() {
        viewModel.isSignInActionEnabled.observe(viewLifecycleOwner) {
            btnSignIn.isEnabled = it
        }
        viewModel.displayLoadingProgressBar.observe(viewLifecycleOwner) {
            loadingProgressBar.visibility = it
        }
        viewModel.navigateToInitialScreenEvent.observe(
            viewLifecycleOwner,
            mainViewModel::navigateTo
        )
        viewModel.displayLoginError.observe(viewLifecycleOwner) { stringId ->
            Toast.makeText(requireContext(), getString(stringId), Toast.LENGTH_SHORT).show()
        }
    }

}