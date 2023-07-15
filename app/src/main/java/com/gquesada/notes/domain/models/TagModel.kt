package com.gquesada.notes.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TagModel(
    val id: Int,
    val title: String
) : Parcelable