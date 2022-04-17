package com.code.apppraytime.difUtill

import androidx.recyclerview.widget.DiffUtil
import com.code.apppraytime.model.HomeModel

class HomeDiffCallback(
    private val oldList: ArrayList<HomeModel?>,
    private val newList: ArrayList<HomeModel?>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]?.id == newList[newItemPosition]?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]?.type == newList[newItemPosition]?.type
    }
}