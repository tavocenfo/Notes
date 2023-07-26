package com.gquesada.notes.ui.main.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gquesada.notes.R
import com.gquesada.notes.ui.addnotes.views.AddNoteFragment
import com.gquesada.notes.ui.main.viewmodels.MainViewModel
import com.gquesada.notes.ui.main.viewmodels.NavigationScreen
import com.gquesada.notes.ui.notes.NoteListFragment
import com.gquesada.notes.ui.tag.views.TagListFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            navigateToFragment(NoteListFragment(), true)
        }
        observe()
    }

    private fun observe() {
        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is NavigationScreen.AddNotes -> navigateToFragment(AddNoteFragment.newInstance(null))
                is NavigationScreen.TagList -> navigateToFragment(TagListFragment())
                is NavigationScreen.EditNote -> navigateToFragment(AddNoteFragment.newInstance(event.noteModel))
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment, isInitialFragment: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (!isInitialFragment) {
            transaction.addToBackStack(fragment.javaClass.name)
        }
        transaction
            .commit()
    }
}