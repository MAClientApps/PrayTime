package com.code.apppraytime.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class VideoRootModel(
    var id: Long = 0,
    val title: String = "",
    var data: ArrayList<VideoChildModel?> = ArrayList()
): Parcelable