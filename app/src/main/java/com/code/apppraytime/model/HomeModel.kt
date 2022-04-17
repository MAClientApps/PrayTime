package com.code.apppraytime.model

import android.os.Parcelable
import com.code.apppraytime.enum.Type
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
data class HomeModel(
    var id: Long = 0,
    val ayah: Int? = null,
    val surah: Int? = null,
    val text: String? = null,
    val image: String? = null,
    val youtube: String? = null,
    var type: Type = Type.TEXT,
): Parcelable
