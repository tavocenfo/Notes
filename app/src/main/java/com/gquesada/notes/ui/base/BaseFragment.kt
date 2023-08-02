package com.gquesada.notes.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gquesada.notes.ui.main.viewmodels.MainViewModel

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    protected val mainViewModel: MainViewModel by activityViewModels()
    protected abstract val viewModel: T

    protected open fun observe() {
        viewModel.hasSessionEnded.observe(viewLifecycleOwner) {
            mainViewModel.onLogOut()
        }
    }
}