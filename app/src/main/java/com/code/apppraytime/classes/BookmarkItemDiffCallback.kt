package com.code.apppraytime.classes

import androidx.recyclerview.widget.DiffUtil
import com.code.apppraytime.data.model.QPModel

class BookmarkItemDiffCallback: DiffUtil.ItemCallback<QPModel>() {

    override fun areItemsTheSame(
        oldItem: QPModel, newItem: QPModel
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: QPModel, newItem: QPModel
    ): Boolean = oldItem.arabic == newItem.arabic
}