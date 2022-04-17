package com.code.apppraytime.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class VideoChildModel(
    val position: Int = 0,
    val title: String = "",
    val videoUrl: String = ""
): Parcelable