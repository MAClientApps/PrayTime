package com.code.apppraytime.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.code.apppraytime.R
import com.code.apppraytime.interfaces.HomeInterface
import com.code.apppraytime.model.VideoChildModel
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.viewModel.PlayerViewModel

class PlayerAdapter(val context: Context, val viewModel: PlayerViewModel,
                    private val homeInterface: HomeInterface
                    ): RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    private var running = 0

    inner class ViewHolder(view: View, val type: Int): RecyclerView.ViewHolder(view) {

        private lateinit var title: TextView
        private lateinit var thumb: ImageView

        init {
            if (type==1) {
                title = view.findViewById(R.id.title)
                thumb = view.findViewById(R.id.thumb)
            }
        }

        fun bind(model: VideoChildModel, play: Boolean) {
            thumb.clipToOutline = true
            itemView.clipToOutline = true
            title.text = model.title

            Glide.with(context).load(getThumb(model.videoUrl))
                .centerCrop().into(thumb)

            itemView.background = ResourcesCompat.getDrawable(
                context.resources,
                if (play) {
                    title.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )
                    R.drawable.playing_item_background
                }
                else {
                    title.setTextColor(
                        Color.parseColor(
                            ApplicationTheme(
                                context as Activity
                            ).getTextColor()
                        )
                    )
                    R.drawable.glass_background
                }, context.theme
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    if (viewType == 0) R.layout.layout_loading
                    else R.layout.layout_player_item,
                    parent, false
                ), viewType
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewModel.videoList.value?.get(position)?.let {
            holder.bind(it, position == viewModel.videoPosition.value ?: 0)
        }

        viewModel.videoPosition.value?.let {
            running = it
        }

        holder.itemView.setOnClickListener {
            if (running != holder.adapterPosition) {
                viewModel.videoPosition.value = holder.adapterPosition
                this@PlayerAdapter.notifyItemChanged(running)
                this@PlayerAdapter.notifyItemChanged(holder.adapterPosition)
            }
        }

        if (position+1 == viewModel.videoList.value?.size)
            homeInterface.onBottom("")
    }

    fun update(pos: Int) {
        if (running != pos) {
            viewModel.videoPosition.value = pos
            this@PlayerAdapter.notifyItemChanged(running)
            this@PlayerAdapter.notifyItemChanged(pos)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewModel.videoList.value!![position]?.let { 1 } ?: 0
    }

    override fun getItemCount(): Int {
        return viewModel.videoList.value?.size ?: 0
    }

    private fun getThumb(id: String): String {
        return "https://i.ytimg.com/vi/$id/mqdefault.jpg"
    }
}