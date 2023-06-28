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
import com.gquesada.notes.domain.usecases.DeleteTagUseCase
import com.gquesada.notes.domain.usecases.EditTagUseCase
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
            withContext(Dispatchers.IO) {
                deleteTagUseCase.execute(TagModel(id = uiTag.id, title = uiTag.name))
            }
        }

    }

    fun editTag(tagName: String, uiTag: UITag?) {
        viewModelScope.launch {
            if (uiTag != null) {
                withContext(Dispatchers.IO) {
                    editTagUseCase.execute(TagModel(uiTag.id, tagName))
                }
            } else {
                withContext(Dispatchers.IO) {
                    addTagUseCase.execute(TagModel(id = 0, tagName))
                }
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