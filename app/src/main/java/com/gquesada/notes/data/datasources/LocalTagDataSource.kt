package com.gquesada.notes.data.datasources

import com.gquesada.notes.data.models.LocalTag

object LocalTagDataSource {

    private val tags = mutableListOf(
        LocalTag(
            id = 1,
            title = "Cenfotec",
        ),
        LocalTag(
            id = 2,
            title = "Hogar",
        ),
    )

    fun getTags(): List<LocalTag> = tags

    fun addTag(tag: LocalTag) {
        tags.add(tag)
    }

    fun removeTag(id: Int) {
        tags.removeIf { tag -> tag.id == id }
    }
}