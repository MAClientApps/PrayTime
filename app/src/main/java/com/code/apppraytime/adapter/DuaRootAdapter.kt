package com.code.apppraytime.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.R
import com.code.apppraytime.interfaces.HomeInterface
import com.code.apppraytime.model.DuaRootModel

class DuaRootAdapter(
    val context: Context, val data: ArrayList<DuaRootModel?>,
    private val homeInterface: HomeInterface
): RecyclerView.Adapter<DuaRootAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(model: DuaRootModel) {
            itemView.findViewById<TextView>(R.id.name).text = model.name
            itemView.findViewById<TextView>(R.id.desc).text = model.desc
            itemView.findViewById<TextView>(R.id.pron).text = model.pron
            itemView.findViewById<TextView>(R.id.trans).text = model.trans
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuaRootAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    if (viewType == 1)
                        R.layout.layout_dua_root
                    else R.layout.layout_loading
                    , parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: DuaRootAdapter.ViewHolder, position: Int) {
        data[position]?.let {
            holder.bind(it)
        }

        if (position == data.size-1 && data.size>1) homeInterface.onBottom(data[position-1]?.id.toString())
    }

    override fun getItemViewType(position: Int) = if (data[position]==null) 0 else 1

    override fun getItemCount() = data.size
}