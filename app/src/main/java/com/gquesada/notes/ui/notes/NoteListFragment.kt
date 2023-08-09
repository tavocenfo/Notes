package com.gquesada.notes.ui.notes

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gquesada.notes.R
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.ui.base.BaseFragment
import com.gquesada.notes.ui.main.viewmodels.MainViewModel
import com.gquesada.notes.ui.main.viewmodels.NavigationScreen
import com.gquesada.notes.ui.main.views.MainActivity
import com.gquesada.notes.ui.notes.adapters.NoteListAdapter
import com.gquesada.notes.ui.notes.viewmodels.NoteListViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NoteListFragment : BaseFragment<NoteListViewModel>() {

    override val viewModel: NoteListViewModel by viewModel()

    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var emptyMessageView: Group
    private lateinit var fabButton: FloatingActionButton
    private val toolbar: MaterialToolbar by lazy {
        (requireActivity() as MainActivity).toolbar
    }

    private val adapter by lazy {
        NoteListAdapter(
            onItemLongClicked = { item -> onLongListItemClicked(item) },
            onItemClicked = { item -> onListItemClicked(item) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_list, container, false)
        initViews(view)
        observe()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewReady(arguments)
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
            toolbar.setTitle(R.string.notes_list_screen_title)
        }
    }

    override fun observe() {
        super.observe()
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
        viewModel.displayErrorMessage.observe(viewLifecycleOwner) { resId ->
            Toast.makeText(requireContext(), getString(resId), Toast.LENGTH_SHORT).show()
        }
        viewModel.navigateToNote.observe(viewLifecycleOwner) { screen ->
            mainViewModel.navigateTo(screen)
        }
    }

    private fun onLongListItemClicked(noteModel: NoteModel) {
        AlertDialog.Builder(context)
            .setTitle(R.string.remove_note_confirm_title)
            .setMessage(getString(R.string.remove_note_confirm_message, noteModel.title))
            .setPositiveButton(R.string.confirm_alert_positive_action) { _, _ ->
                viewModel.deleteNote(noteModel)
            }
            .setNegativeButton(R.string.confirm_alert_negative_action, null)
            .show()
    }

    private fun onListItemClicked(noteModel: NoteModel) {
        mainViewModel.navigateTo(NavigationScreen.EditNote(noteModel))
    }

}