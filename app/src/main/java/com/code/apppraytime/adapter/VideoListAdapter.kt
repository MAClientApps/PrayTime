package com.code.apppraytime.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.code.apppraytime.R
import com.code.apppraytime.interfaces.HomeInterface
import com.code.apppraytime.model.VideoChildModel
import com.code.apppraytime.screen.PlayerActivity

class VideoListAdapter(val context: Context, val data: ArrayList<VideoChildModel?>,
                       val title: String, val hid: String,
                       private val homeInterface: HomeInterface)
    : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {

    inner class  ViewHolder(view: View, type: Int): RecyclerView.ViewHolder(view) {
        private lateinit var title: TextView
        private lateinit var thumb: ImageView

        init {
            if (type==1) {
                title = view.findViewById(R.id.title)
                thumb = view.findViewById(R.id.thumb)
            }
        }

        fun bind(model: VideoChildModel) {
            thumb.clipToOutline = true
            itemView.clipToOutline = true
            title.text = model.title
            Glide.with(context).load(getThumb(model.videoUrl))
                .centerCrop().into(thumb)

            itemView.background = ResourcesCompat.getDrawable(
                context.resources, R.drawable.glass_background, context.theme
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    if (viewType == 0) R.layout.layout_loading
                    else R.layout.layout_video_list_item,
                    parent, false
                ), viewType
        )
    }

    override fun onBindViewHolder(holder: VideoListAdapter.ViewHolder, position: Int) {
        data[position]?.let {
            holder.bind(it)
        }

        holder.itemView.setOnClickListener {
            PlayerActivity.launch(context, position, title, hid, data)
        }

        if (position+1 == data.size) homeInterface.onBottom("")
    }

    override fun getItemViewType(position: Int): Int {
        return data[position]?.let { 1 } ?: 0
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getThumb(id: String): String {
        return "https://i.ytimg.com/vi/$id/mqdefault.jpg"
    }
}