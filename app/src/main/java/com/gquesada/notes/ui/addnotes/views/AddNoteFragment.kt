package com.gquesada.notes.ui.addnotes.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gquesada.notes.R
import com.gquesada.notes.data.database.database.AppDatabase
import com.gquesada.notes.data.datasources.DatabaseNoteDataSource
import com.gquesada.notes.data.datasources.LocalNoteDataSource
import com.gquesada.notes.data.repositories.NoteRepositoryImpl
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.usecases.AddNoteUseCase
import com.gquesada.notes.domain.usecases.EditNoteUseCase
import com.gquesada.notes.ui.addnotes.viewmodels.AddNoteViewModel
import com.gquesada.notes.ui.addnotes.viewmodels.factories.AddNoteViewModelFactory
import com.gquesada.notes.ui.main.viewmodels.MainViewModel
import com.gquesada.notes.ui.main.viewmodels.NavigationScreen
import com.gquesada.notes.ui.tag.views.TagListFragment.Companion.TAG_ADDED_REQUEST_KEY


class AddNoteFragment private constructor() : Fragment() {

    private lateinit var tagCell: View
    private lateinit var tvTagName: TextView
    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: AddNoteViewModel
    private lateinit var toolbar: MaterialToolbar

    private val tagDao by lazy { AppDatabase.getInstance(requireContext()).getTagDao() }
    private val noteDao by lazy { AppDatabase.getInstance(requireContext()).getNotesDao() }
    private val noteDataSource by lazy { DatabaseNoteDataSource(tagDao, noteDao) }
    private val viewModelFactory: AddNoteViewModelFactory by lazy {
        AddNoteViewModelFactory(
            AddNoteUseCase(NoteRepositoryImpl(noteDataSource)),
            EditNoteUseCase(NoteRepositoryImpl(noteDataSource))
        )
    }


    companion object {

        private const val NOTE_MODEL_KEY = "NOTE_MODEL_KEY"
        fun newInstance(noteModel: NoteModel?): AddNoteFragment {
            val bundle = bundleOf(NOTE_MODEL_KEY to noteModel)
            val fragment = AddNoteFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(TAG_ADDED_REQUEST_KEY) { requestKey, bundle ->
            val tag = bundle.getParcelable<TagModel>(requestKey) ?: return@setFragmentResultListener
            viewModel.setTag(tag)
        }
        viewModel = ViewModelProvider(this, viewModelFactory)[AddNoteViewModel::class.java]
        viewModel.setNoteModel(arguments?.getParcelable(NOTE_MODEL_KEY))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_note, container, false)
        initViews(view)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
    }

    private fun initViews(view: View) {
        with(view) {
            tvTagName = findViewById(R.id.tv_tag_name)
            edtTitle = findViewById(R.id.edt_title)
            edtDescription = findViewById(R.id.edt_description)
            tagCell = findViewById(R.id.tag_cell)
            tagCell.setOnClickListener {
                mainViewModel.navigateTo(NavigationScreen.TagList)
            }
            fabAddNote = findViewById(R.id.fab_add_note)
            fabAddNote.setOnClickListener {
                viewModel.addNote(edtTitle.text.toString(), edtDescription.text.toString())
            }
            toolbar = findViewById(R.id.toolbar)
        }
    }

    private fun observe() {
        viewModel.tagAddedLiveData.observe(viewLifecycleOwner) { tag ->
            tvTagName.text = tag.title
        }
        viewModel.displayErrorMessageLiveData.observe(viewLifecycleOwner) { errorRes ->
            Toast.makeText(requireContext(), getString(errorRes), Toast.LENGTH_LONG).show()
        }
        viewModel.noteAddedLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), R.string.add_note_success_message, Toast.LENGTH_LONG)
                .show()
            parentFragmentManager.popBackStack()
        }
        viewModel.screenTitleLiveData.observe(viewLifecycleOwner) { stringId ->
            toolbar.title = getString(stringId)
        }
        viewModel.noteLiveData.observe(viewLifecycleOwner) { noteModel ->
            edtTitle.setText(noteModel.title)
            edtDescription.setText(noteModel.description)
        }
        viewModel.noteUpdatedLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), R.string.add_note_success_message, Toast.LENGTH_LONG)
                .show()
            parentFragmentManager.popBackStack()
        }
    }
}