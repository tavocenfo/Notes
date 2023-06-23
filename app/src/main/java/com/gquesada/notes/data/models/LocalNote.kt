package com.gquesada.notes.data.models


data class LocalNote(
    val id: Long,
    val title: String,
    val description: String?,
    val tag: LocalTag,
    val date: Long
)
