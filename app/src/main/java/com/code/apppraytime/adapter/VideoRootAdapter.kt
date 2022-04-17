package com.code.apppraytime.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.R
import com.code.apppraytime.interfaces.LearnRoot
import com.code.apppraytime.model.VideoRootModel

class VideoRootAdapter(
    val context: Context
    , private val learnRoot: LearnRoot,
    val data: ArrayList<VideoRootModel?>?
    ) : RecyclerView.Adapter<VideoRootAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(model: VideoRootModel) {
            view.findViewById<TextView>(R.id.course_title).text = model.title
            view.findViewById<RecyclerView>(R.id.recycler_child).run {
                layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.HORIZONTAL, false
                )
                adapter = VideoChildAdapter(context, model.title, model.id.toString(), model.data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoRootAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    if (viewType == 0) R.layout.layout_loading
                    else R.layout.layout_video_root,
                    parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: VideoRootAdapter.ViewHolder, position: Int) {
        data?.get(position)?.let {
            holder.bind(it)
        }
        if (position+1==data?.size) learnRoot.onBottom()
    }

    override fun getItemViewType(position: Int): Int {
        return data?.get(position)?.let { 1 } ?: 0
    }

    override fun getItemCount() = data?.size ?: 0

}