package com.code.apppraytime.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.R
import com.code.apppraytime.classes.AudioProcess
import com.code.apppraytime.model.SearchModel
import com.code.apppraytime.screen.SurahActivity
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.BookmarkHelper
import com.code.apppraytime.sql.IndexHelper

class SearchAdapter(val context: Context, val data: ArrayList<SearchModel>):
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private val bookmarkDB = BookmarkHelper(context)

    inner class ViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder(view) {
        var count: TextView? = null
        var name: TextView? = null
        var from: TextView? = null

        var arabic: TextView? = null

        var play: ImageView? = null
        var pron: TextView? = null
        var share: ImageView? = null
        var ayatNo: TextView? = null
        var surahName: TextView? = null
        var bookmark: ImageView? = null
        var translation: TextView? = null

        init {
            when (viewType) {
                SURAH -> {
                    count = view.findViewById(R.id.count)
                    name = view.findViewById(R.id.name)
                    from = view.findViewById(R.id.from)
                    arabic = view.findViewById(R.id.arabic)
                }
                else -> {
                    pron = view.findViewById(R.id.pron)
                    play = view.findViewById(R.id.play)
                    share = view.findViewById(R.id.share)
                    arabic = view.findViewById(R.id.arabic)
                    ayatNo = view.findViewById(R.id.ayat_no)
                    bookmark = view.findViewById(R.id.bookmark)
                    surahName = view.findViewById(R.id.surah_name)
                    translation = view.findViewById(R.id.translation)

                    play!!.clipToOutline = true
                    share!!.clipToOutline = true
                    bookmark!!.clipToOutline = true

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    when(viewType) {
                        SURAH -> R.layout.layout_surah_name
                        else -> R.layout.layout_ayat
                    }, parent, false
                ), viewType
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            SURAH -> {
                holder.count?.text = data[position].pos.toString()
                holder.name?.text = data[position].name
                holder.from?.text = "${data[position].revelation}   |   ${data[position].verse} VERSES"
                holder.arabic?.text = data[position].nameAr

                holder.itemView.setOnClickListener {
                    SurahActivity.launch(context, data[position].pos, 0)
                }
            }
            else -> {
                data[position].let {
                    holder.run {
                        ayatNo?.text = it.ayat.toString()
                        arabic?.text = textToHtml(it.arabic)

//                        if (Application(context).transliteration) {
//                            pron?.text = it.pronunciation
//                            pron?.visibility = View.VISIBLE
//                        } else {
//                            pron?.visibility = View.GONE
//                        }

                        if (Application(context).transliteration) {
                            pron?.text = it.pronunciation
                            pron?.visibility = View.VISIBLE
                            translation?.visibility = View.VISIBLE
                            translation?.text = textToHtml(it.translation)
                        } else {
                            translation?.visibility = View.GONE
                            pron?.visibility = View.VISIBLE
                            pron?.text = textToHtml(it.translation)
                        }

                        surahName?.text = IndexHelper(context).readSurahX(it.surah)?.name

//                        translation?.visibility = View.VISIBLE
//                        translation?.text = textToHtml(it.translation)

                        maintainClicks(share, play, bookmark, holder, it)

                        arabic?.setTextSize(TypedValue.COMPLEX_UNIT_SP, Application(context).arabicFontSize)
                        pron?.setTextSize(TypedValue.COMPLEX_UNIT_SP, Application(context).transliterationFontSize)
                        translation?.setTextSize(TypedValue.COMPLEX_UNIT_SP, Application(context).translationFontSize)
                    }
                }
            }
        }
    }

    private fun maintainClicks(
        share: ImageView?,
        play: ImageView?,
        bookmark: ImageView?,
        holder: ViewHolder,
        model: SearchModel) {
        share?.setOnClickListener { v->
            var text = "${context.resources.getString(R.string.surah)}: ${model.surah}, " +
                    "${context.resources.getString(R.string.ayat)}: ${model.ayat}\n\n"
            text += model.arabic
            text += "\n\n${context.resources.getString(R.string.meaning)} " +
                    ":  "+model.translation

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }

        bookmark?.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                if (bookmarkDB.onRead(model.pos))
                    R.drawable.ic_baseline_bookmark_24
                else R.drawable.ic_baseline_bookmark_border_24,
                null
            )
        )

        bookmark?.setOnClickListener { _ ->
            if (bookmarkDB.onRead(model.pos)) {
                bookmarkDB.onDelete(model.pos)
            } else bookmarkDB.onWrite(model.pos)
            bookmark.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    if (bookmarkDB.onRead(model.pos))
                        R.drawable.ic_baseline_bookmark_24
                    else R.drawable.ic_baseline_bookmark_border_24,
                    null
                )
            )
        }

        holder.itemView.setOnClickListener { _ ->
            SurahActivity.launch(context, model.surah, model.ayat)
        }

        play?.setOnClickListener {
            AudioProcess(context as Activity).play(model.surah, model.ayat)
        }
    }
//
//    private fun modelExchange(temp: SearchModel): Quran {
//        return Quran(
//            pos = temp.pos,
//            surah = temp.surah,
//            ayat = temp.ayat,
//            indopak = temp.indopak,
//            utsmani = temp.utsmani,
//            jalalayn = temp.jalalayn,
//            latin = temp.latin,
//            terjemahan = temp.terjemahan,
//            englishPro = temp.englishPro,
//            englishT = temp.englishT
//        )
//    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun textToHtml(it: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
        else Html.fromHtml(it)
    }

    companion object {
        const val AYAT = 1
        const val SURAH = 0
    }
}