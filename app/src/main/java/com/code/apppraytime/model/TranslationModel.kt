package com.code.apppraytime.model

import androidx.annotation.Keep

@Keep
data class TranslationModel(
    val id: Int,
    val surah: Int,
    val ayah: Int,
    var text: String
)
