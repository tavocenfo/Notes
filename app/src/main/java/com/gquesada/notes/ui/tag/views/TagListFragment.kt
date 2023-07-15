package com.gquesada.notes.ui.tag.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.gquesada.notes.R
import com.gquesada.notes.data.datasources.LocalTagDataSource
import com.gquesada.notes.data.repositories.TagRepositoryImpl
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.usecases.AddTagUseCase
import com.gquesada.notes.domain.usecases.DeleteTagUseCase
import com.gquesada.notes.domain.usecases.EditTagUseCase
import com.gquesada.notes.domain.usecases.GetTagListUseCase
import com.gquesada.notes.ui.tag.adapters.TagListAdapter
import com.gquesada.notes.ui.tag.models.UITag
import com.gquesada.notes.ui.tag.viewmodels.EditAlertUIState
import com.gquesada.notes.ui.tag.viewmodels.ScreenMode
import com.gquesada.notes.ui.tag.viewmodels.TagListViewModel
import com.gquesada.notes.ui.tag.viewmodels.factories.TagListViewModelFactory

private const val EDIT_MENU_ITEM = 1
private const val REMOVE_MENU_ITEM = 2

class TagListFragment : Fragment() {

    companion object {
        const val TAG_ADDED_REQUEST_KEY = "TAG_ADDED_REQUEST_KEY"
    }

    private val adapter by lazy { TagListAdapter(::onTagSelected) }
    private val repository by lazy { TagRepositoryImpl(LocalTagDataSource) }
    private val getTagListUseCase by lazy { GetTagListUseCase(repository) }
    private val deleteTagUseCase by lazy { DeleteTagUseCase(repository) }
    private val addTagUseCase by lazy { AddTagUseCase(repository) }
    private val editTagUseCase by lazy { EditTagUseCase(repository) }
    private val viewModelFactory by lazy {
        TagListViewModelFactory(
            getTagListUseCase,
            deleteTagUseCase,
            addTagUseCase,
            editTagUseCase
        )
    }


    private lateinit var viewModel: TagListViewModel
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fabAddAction: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tag_list, container, false)
        initViews(view)
        viewModel = ViewModelProvider(this, viewModelFactory)[TagListViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
    }

    private fun initViews(view: View) {
        with(view) {
            toolbar = findViewById(R.id.toolbar)
            tagsRecyclerView = findViewById(R.id.tag_list)
            tagsRecyclerView.adapter = adapter
            tagsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            tagsRecyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    RecyclerView.VERTICAL
                )
            )
            fabAddAction = findViewById(R.id.fab_add_tag)
            fabAddAction.setOnClickListener {
                viewModel.onSaveTag()
            }
        }
    }

    private fun observe() {
        viewModel.tagListLiveData.observe(viewLifecycleOwner) { data ->
            tagsRecyclerView.post {
                adapter.setData(data)
            }
        }
        viewModel.screenModeLiveData.observe(viewLifecycleOwner) { menuMode ->
            when (menuMode) {
                ScreenMode.ReadMode -> setAddMode()
                ScreenMode.EditMode -> setEditMode()
                else -> Unit
            }
        }
        viewModel.showDeleteMessageLiveData.observe(viewLifecycleOwner, ::showRemoveConfirmMessage)
        viewModel.showEditAlertLiveData.observe(viewLifecycleOwner, ::showEditAlertDialog)
        viewModel.tagSavedLiveData.observe(viewLifecycleOwner, ::tagSaved)
    }

    private fun tagSaved(tag: TagModel) {
        setFragmentResult(TAG_ADDED_REQUEST_KEY, bundleOf(TAG_ADDED_REQUEST_KEY to tag))
        parentFragmentManager.popBackStack()
    }

    private fun onTagSelected(uiTag: UITag) {
        viewModel.onTagSelected(uiTag)
    }

    private fun setAddMode() {
        fabAddAction.visibility = View.GONE
        toolbar.menu.findItem(R.id.add_item).setOnMenuItemClickListener {
            viewModel.onAddItemSelected()
            true
        }
        toolbar.menu.removeItem(EDIT_MENU_ITEM)
        toolbar.menu.removeItem(REMOVE_MENU_ITEM)
    }

    private fun setEditMode() {
        fabAddAction.visibility = View.VISIBLE
        addMenuEditItem(toolbar.menu)
        addMenuRemoveItem(toolbar.menu)
    }

    private fun addMenuEditItem(menu: Menu) {
        menu.add(0, EDIT_MENU_ITEM, Menu.NONE, R.string.menu_edit_item)
            .setIcon(R.drawable.ic_pencil_edit_24)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
        menu.findItem(EDIT_MENU_ITEM).setOnMenuItemClickListener {
            viewModel.onEditItemSelected()
            true
        }
    }

    private fun addMenuRemoveItem(menu: Menu) {
        menu.add(0, REMOVE_MENU_ITEM, Menu.NONE, R.string.menu_remove_item)
            .setIcon(R.drawable.ic_delete_24)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)


        menu.findItem(REMOVE_MENU_ITEM).setOnMenuItemClickListener {
            viewModel.confirmRemoveItem()
            true
        }
    }

    private fun showRemoveConfirmMessage(uiTag: UITag) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.remove_tag_confirm_title)
            .setMessage(getString(R.string.remove_tag_confirm_message, uiTag.name))
            .setPositiveButton(R.string.confirm_alert_positive_action) { _, _ ->
                viewModel.onRemoveItem(uiTag)
            }
            .setNegativeButton(R.string.confirm_alert_negative_action, null)
            .show()
    }

    private fun showEditAlertDialog(uiState: EditAlertUIState) {
        var tagNameEditText: TextInputEditText? = null
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(uiState.title)
            .setView(R.layout.edit_tag_layout)
            .setPositiveButton(uiState.positiveButton) { _, _ -> }
            .setNegativeButton(R.string.confirm_alert_negative_action, null)
            .create()
        dialog.setOnShowListener {
            tagNameEditText = dialog.findViewById(R.id.edt_tag_name)
            tagNameEditText?.setText(uiState.uiTag?.name)
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val tagName = tagNameEditText?.text?.toString() ?: ""
            if (tagName.isEmpty()) {
                tagNameEditText?.error = getString(R.string.edit_tag_empty_name_error)
            } else {
                viewModel.editTag(tagName, uiState.uiTag)
                dialog.dismiss()
            }
        }
    }

}