package com.code.apppraytime.classes

import androidx.recyclerview.widget.DiffUtil
import com.code.apppraytime.model.SearchModel

class SearchDiff(private val oldList: ArrayList<SearchModel>,
                 private val newList: ArrayList<SearchModel>
                 ) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].pos == newList[newItemPosition].pos
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].ayat == newList[newItemPosition].ayat
    }
}