package com.gquesada.notes.ui.tag.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.gquesada.notes.R
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.usecases.AddTagUseCase
import com.gquesada.notes.domain.usecases.AddTagUseCaseOutput
import com.gquesada.notes.domain.usecases.DeleteTagUseCase
import com.gquesada.notes.domain.usecases.DeleteTagUseCaseOutput
import com.gquesada.notes.domain.usecases.EditTagUseCase
import com.gquesada.notes.domain.usecases.EditTagUseCaseOutput
import com.gquesada.notes.domain.usecases.GetTagListUseCase
import com.gquesada.notes.ui.tag.models.UITag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TagListViewModel(
    private val getTagListUseCase: GetTagListUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
    private val addTagUseCase: AddTagUseCase,
    private val editTagUseCase: EditTagUseCase
) : ViewModel() {

    private val _tagListLiveData = MutableLiveData<List<UITag>>()
    val tagListLiveData: LiveData<List<UITag>>
        get() = _tagListLiveData
    private val _showDeleteMessageLiveData = MutableLiveData<UITag>()
    val showDeleteMessageLiveData: LiveData<UITag>
        get() = _showDeleteMessageLiveData
    private val _showEditAlertLiveData = MutableLiveData<EditAlertUIState>()
    val showEditAlertLiveData: LiveData<EditAlertUIState>
        get() = _showEditAlertLiveData
    private val _tagSavedLiveData = MutableLiveData<TagModel>()
    val tagSavedLiveData: LiveData<TagModel>
        get() = _tagSavedLiveData

    private val _displayErrorMessage: MutableLiveData<Int> = MutableLiveData()
    val displayErrorMessage: LiveData<Int>
        get() = _displayErrorMessage

    private val _displaySuccessMessage: MutableLiveData<Int> = MutableLiveData()
    val displaySuccessMessage: LiveData<Int>
        get() = _displaySuccessMessage

    val screenModeLiveData: LiveData<ScreenMode> = tagListLiveData.map { data ->
        val isItemChecked = data.any { item -> item.isChecked }
        if (isItemChecked) ScreenMode.EditMode else ScreenMode.ReadMode
    }.distinctUntilChanged()


    init {
        getTags()
    }

    private fun getTags() {
        viewModelScope.launch {
            getTagListUseCase.execute()
                .map { items ->
                    items.map { item ->
                        UITag(
                            id = item.id,
                            name = item.title,
                            isChecked = false
                        )
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { tags ->
                    _tagListLiveData.value = tags
                }

        }
    }

    fun onTagSelected(uiTag: UITag) {
        val list = tagListLiveData.value?.map { item ->
            if (item.id == uiTag.id) {
                item.copy(isChecked = !uiTag.isChecked)
            } else {
                item.copy(isChecked = false)
            }
        }
        _tagListLiveData.value = list ?: listOf()
    }

    fun onAddItemSelected() {
        _showEditAlertLiveData.value = EditAlertUIState(
            title = R.string.add_tag_alert_title,
            positiveButton = R.string.add_tag_positive_action,
            uiTag = null
        )
    }

    fun onEditItemSelected() {
        val tag = tagListLiveData.value?.find { item -> item.isChecked } ?: return
        _showEditAlertLiveData.value = EditAlertUIState(
            title = R.string.edit_tag_alert_title,
            positiveButton = R.string.edit_tag_positive_action,
            uiTag = tag
        )
    }

    fun confirmRemoveItem() {
        val tag = tagListLiveData.value?.find { item -> item.isChecked } ?: return
        _showDeleteMessageLiveData.value = tag
    }

    fun onRemoveItem(uiTag: UITag) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                deleteTagUseCase.execute(TagModel(id = uiTag.id, title = uiTag.name))
            }
            if (result is DeleteTagUseCaseOutput.Error) {
                _displayErrorMessage.value = R.string.error_removing_tag_message
            }
        }

    }

    fun editTag(tagName: String, uiTag: UITag?) {
        if (uiTag != null) {
            updateTag(TagModel(uiTag.id, tagName))
        } else {
            insertTag(TagModel(id = 0, tagName))
        }
    }

    private fun insertTag(tagModel: TagModel) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                addTagUseCase.execute(tagModel)
            }
            when (result) {
                is AddTagUseCaseOutput.Success -> _displaySuccessMessage.value =
                    R.string.tag_added_success_message

                is AddTagUseCaseOutput.Error -> _displayErrorMessage.value =
                    R.string.error_adding_tag_message
            }
        }
    }

    private fun updateTag(tagModel: TagModel) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                editTagUseCase.execute(tagModel)
            }
            when (result) {
                is EditTagUseCaseOutput.Success -> _displaySuccessMessage.value =
                    R.string.tag_updated_success_message

                is EditTagUseCaseOutput.Error -> _displayErrorMessage.value =
                    R.string.error_updating_tag_message
            }
        }
    }

    fun onSaveTag() {
        val tag = tagListLiveData.value?.find { item -> item.isChecked } ?: return
        _tagSavedLiveData.value = TagModel(
            id = tag.id,
            title = tag.name
        )
    }

}

enum class ScreenMode {
    ReadMode,
    EditMode
}

data class EditAlertUIState(
    @StringRes val title: Int,
    @StringRes val positiveButton: Int,
    val uiTag: UITag?
)