package com.gquesada.notes.ui.tag.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.gquesada.notes.R
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.usecases.DeleteTagUseCase
import com.gquesada.notes.domain.usecases.GetTagListUseCase
import com.gquesada.notes.ui.tag.models.UITag

class TagListViewModel(
    private val getTagListUseCase: GetTagListUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
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
        _tagListLiveData.value = getTagListUseCase.execute()
            .map { item ->
                UITag(
                    id = item.id,
                    name = item.title,
                    isChecked = false
                )
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
        deleteTagUseCase.execute(uiTag.id)
        val list = _tagListLiveData.value?.filter { it.id != uiTag.id } ?: return
        _tagListLiveData.value = list
    }

    fun editTag(tagName: String, uiTag: UITag?) {
        val list = uiTag?.let { tag ->
            _tagListLiveData.value?.map { item ->
                if (item.id == tag.id) {
                    tag.copy(name = tagName)
                } else {
                    item
                }
            }
        } ?: run {
            val newList = _tagListLiveData.value?.toMutableList()
            newList?.add(UITag(id = 0, name = tagName, isChecked = false))
            newList?.toList() ?: emptyList()
        }
        _tagListLiveData.value = list
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