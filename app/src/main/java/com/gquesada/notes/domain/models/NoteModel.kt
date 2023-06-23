package com.gquesada.notes.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteModel(
    val id: Long,
    val title: String,
    val description: String?,
    val tag: TagModel,
    val date: Long
) : Parcelable
