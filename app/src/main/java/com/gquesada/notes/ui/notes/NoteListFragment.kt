package com.gquesada.notes.ui.notes

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gquesada.notes.R
import com.gquesada.notes.data.datasources.LocalNoteDataSource
import com.gquesada.notes.data.repositories.NoteRepositoryImpl
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.usecases.DeleteNoteUseCase
import com.gquesada.notes.domain.usecases.GetNotesUseCase
import com.gquesada.notes.ui.main.viewmodels.MainViewModel
import com.gquesada.notes.ui.main.viewmodels.NavigationScreen
import com.gquesada.notes.ui.notes.adapters.NoteListAdapter
import com.gquesada.notes.ui.notes.viewmodels.NoteListViewModel
import com.gquesada.notes.ui.notes.viewmodels.factories.NoteListViewModelFactory


class NoteListFragment : Fragment() {

    private val repository by lazy { NoteRepositoryImpl(noteDataSource = LocalNoteDataSource) }

    private val getNoteListUseCase by lazy { GetNotesUseCase(repository) }
    private val deleteNoteUseCase by lazy { DeleteNoteUseCase(repository) }

    private val viewModelFactory by lazy {
        NoteListViewModelFactory(
            getNoteListUseCase,
            deleteNoteUseCase
        )
    }
    private lateinit var viewModel: NoteListViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var emptyMessageView: Group
    private lateinit var fabButton: FloatingActionButton

    private val adapter by lazy {
        NoteListAdapter(
            onItemLongClicked = { item -> onListItemClicked(item) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_list, container, false)
        initViews(view)
        viewModel = ViewModelProvider(this, viewModelFactory)[NoteListViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        observe()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewReady()
    }

    private fun initViews(view: View) {
        with(view) {
            notesRecyclerView = findViewById(R.id.notes_list)
            notesRecyclerView.adapter = adapter
            notesRecyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    RecyclerView.VERTICAL
                )
            )
            notesRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            emptyMessageView = findViewById(R.id.no_notes_group)
            fabButton = findViewById(R.id.floatingActionButton)
            fabButton.setOnClickListener { mainViewModel.navigateTo(NavigationScreen.AddNotes) }
        }
    }

    private fun observe() {
        viewModel.noteListLiveData.observe(viewLifecycleOwner) { list ->
            adapter.setData(list)
            if (list.isEmpty()) {
                emptyMessageView.visibility = View.VISIBLE
                notesRecyclerView.visibility = View.GONE
            } else {
                emptyMessageView.visibility = View.GONE
                notesRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun onListItemClicked(noteModel: NoteModel) {
        AlertDialog.Builder(context)
            .setTitle(R.string.remove_note_confirm_title)
            .setMessage(getString(R.string.remove_note_confirm_message, noteModel.title))
            .setPositiveButton(R.string.confirm_alert_positive_action) { _, _ ->
                viewModel.deleteNote(noteModel)
            }
            .setNegativeButton(R.string.confirm_alert_negative_action, null)
            .show()
    }

}