package com.code.apppraytime.classes

import androidx.recyclerview.widget.DiffUtil
import com.code.apppraytime.data.model.QPModel

class BookmarkDiff (
    private val oldList: ArrayList<QPModel>,
    private val newList: ArrayList<QPModel>
    ) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].arabic == newList[newItemPosition].arabic
    }
}