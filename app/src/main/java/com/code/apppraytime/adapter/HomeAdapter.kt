package com.code.apppraytime.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.code.apppraytime.R
import com.code.apppraytime.data.QuranData
import com.code.apppraytime.difUtill.HomeDiffCallback
import com.code.apppraytime.enum.Type
import com.code.apppraytime.interfaces.HomeInterface
import com.code.apppraytime.model.HomeModel
import com.code.apppraytime.screen.SurahActivity
import com.code.apppraytime.screen.YoutubeActivity

class HomeAdapter(
    val context: Context,
    initData: ArrayList<HomeModel?>?,
    private val homeInterface: HomeInterface
    ) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private val data = ArrayList<HomeModel?>()

    private val quranData by lazy {
        QuranData(context)
    }

    init { initData?.let { data.addAll(it) } }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindImage(model: HomeModel) {
            itemView.findViewById<ImageView>(R.id.image).let {
                it.clipToOutline = true
                Glide.with(context).load(model.image).centerCrop().into(it)
            }
        }

        fun bindText(model: HomeModel) {
            itemView.findViewById<TextView>(R.id.text).text = model.text
        }

        fun bindAyah(model: HomeModel) {
            itemView.findViewById<TextView>(R.id.title).text = model.text

            model.surah?.let { s->
                itemView.findViewById<TextView>(R.id.text).text =
                    quranData.getSurah(s)[model.ayah!!].translation

                itemView.setOnClickListener {
                    SurahActivity.launch(
                        context, s, model.ayah
                    )
                }
            }
        }

        fun bindYoutube(model: HomeModel) {
            itemView.findViewById<ImageView>(R.id.image).let {
                it.clipToOutline = true
                Glide.with(context).load(
                    getThumb(model.youtube!!)
                ).centerCrop().into(it)
            }

            itemView.findViewById<TextView>(R.id.text).text = model.text

            itemView.setOnClickListener {
                YoutubeActivity.launch(context, model.youtube!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    when (viewType) {
                        IMAGE -> R.layout.layout_home_image
                        TEXT -> R.layout.layout_home_text
                        AYAH -> R.layout.layout_home_ayah
                        YOUTUBE -> R.layout.layout_home_youtube
                        FINISHED -> R.layout.layout_finished
                        else -> R.layout.layout_loading
                    }, parent, false
                )
        )

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
        data[position]?.let {
            when(it.type) {
                Type.IMAGE -> holder.bindImage(it)
                Type.TEXT -> holder.bindText(it)
                Type.AYAH -> holder.bindAyah(it)
                Type.YOUTUBE -> holder.bindYoutube(it)
            }
        }
        if (position == data.size-1) homeInterface.onBottom(data[position]?.id.toString())
    }

    override fun getItemViewType(position: Int) =
        when(data[position]?.type) {
            Type.IMAGE -> IMAGE
            Type.TEXT -> TEXT
            Type.AYAH -> AYAH
            Type.YOUTUBE ->  YOUTUBE
            Type.FINISHED -> FINISHED
            else -> 0
    }

    override fun getItemCount() = data.size

    fun swap(newData: ArrayList<HomeModel?>) {
        val diffCallback = HomeDiffCallback(data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getThumb(id: String) = "https://i.ytimg.com/vi/$id/hqdefault.jpg"

    companion object {
        const val IMAGE = 1
        const val TEXT = 2
        const val YOUTUBE = 3
        const val AYAH = 4
        const val FINISHED = 10
    }
}