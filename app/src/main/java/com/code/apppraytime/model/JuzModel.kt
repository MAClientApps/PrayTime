package com.code.apppraytime.model

import androidx.annotation.Keep

@Keep
data class JuzModel(
    val id: Int,
    val name: String,
    val ayatStart: Int,
    val start: Int,
    val end: Int,
    val size: Int
)