package com.code.apppraytime.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.code.apppraytime.R
import com.code.apppraytime.model.VideoChildModel
import com.code.apppraytime.screen.PlayerActivity
import com.code.apppraytime.screen.VideoListActivity

class VideoChildAdapter(val context: Context, val title: String,
                        val lid: String, val data: ArrayList<VideoChildModel?>)
    : RecyclerView.Adapter<VideoChildAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        fun bind(pos: Int, model: VideoChildModel) {

            view.run {
                findViewById<TextView>(R.id.item_title).run {
                    text = model.title
                }
                findViewById<ImageView>(R.id.thumbnail).run {
                    clipToOutline = true
                    Glide.with(context).load(getThumb(model.videoUrl))
                        .centerCrop().into(this)
                }
                setOnClickListener {
                    PlayerActivity.launch(context, pos, title, lid, data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoChildAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    if (viewType==1)
                        R.layout.layout_video
                    else R.layout.layout_next
                    , parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: VideoChildAdapter.ViewHolder, position: Int) {
        data[position]?.let {
            holder.bind(position, it)
        } ?: kotlin.run {
            holder.itemView.setOnClickListener {
                VideoListActivity.launch(context, title, lid)
            }
        }
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