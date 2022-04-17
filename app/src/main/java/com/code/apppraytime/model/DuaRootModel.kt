package com.code.apppraytime.model

import androidx.annotation.Keep

@Keep
data class DuaRootModel(
    val id: Long = 0L,
    val total: Int = 0,
    val name: String = "",
    val desc: String = "",
    val pron: String = "",
    val trans: String = ""
)