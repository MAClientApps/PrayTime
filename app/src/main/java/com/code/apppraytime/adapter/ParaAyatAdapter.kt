package com.code.apppraytime.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.R
import com.code.apppraytime.classes.AudioProcess
import com.code.apppraytime.data.model.QPModel
import com.code.apppraytime.constant.Name.surahMeaning
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.BookmarkHelper
import com.code.apppraytime.external.ShareSheet
import com.code.apppraytime.tajweed.exporter.HtmlExporter
import com.code.apppraytime.tajweed.model.Result
import com.code.apppraytime.tajweed.model.ResultUtil
import com.code.apppraytime.tajweed.model.TajweedRule
import java.text.NumberFormat
import java.util.*

class ParaAyatAdapter(val context: Context, val data: ArrayList<QPModel>)
    : RecyclerView.Adapter<ParaAyatAdapter.ViewHolder>() {

    private val exporter = HtmlExporter()
    private val application = Application(context)
    private val bookmarkDB = BookmarkHelper(context)
    private val shareSheet by lazy { ShareSheet(context) }

    inner class ViewHolder(view: View,  val type: Int): RecyclerView.ViewHolder(view) {
        var pron: TextView? = null
        var play: ImageView? = null
        var share: ImageView? = null
        var arabic: TextView? = null
        var ayatNo: TextView? = null
        var surahName: TextView? = null
        var bookmark: ImageView? = null
        var translation: TextView? = null

        init {
            if (type > 0) {
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

        fun bind(model: QPModel) {
            ayatNo?.text = numberFormat.format(model.ayat)
            arabic?.text = if (application.tajweed)
                html(model.arabic) else model.arabic
            surahName?.text = model.name

            if (Application(context).transliteration) {
                pron?.text = model.pronunciation
                pron?.visibility = View.VISIBLE
                translation?.visibility = View.VISIBLE
                translation?.text = model.translation
            } else {
                translation?.visibility = View.GONE
                pron?.visibility = View.VISIBLE
                pron?.text = model.translation
            }

            maintainClicks(share, bookmark, play, model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ParaAyatAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    if (viewType == 0)
                        R.layout.layout_ayat_header
                    else R.layout.layout_ayat,
                    parent, false
                ), viewType
        )
    }

    private fun html(text: String): Spanned {
        val results: MutableList<Result> = ArrayList()
        TajweedRule.MADANI_RULES.forEach {
            results.addAll(it.rule.checkAyah(text))
        }
        ResultUtil.INSTANCE.sort(results)
        return HtmlCompat.fromHtml(exporter.export(text, results), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ParaAyatAdapter.ViewHolder, position: Int) {
        data[position].let {
            if (getItemViewType(position) > 0)
                holder.bind(it)
            else {
                holder.itemView.run {
                    findViewById<TextView>(R.id.name).text = it.translation
                    findViewById<TextView>(R.id.meaning).text = surahMeaning[it.surah-1]
                    findViewById<ImageView>(R.id.bismillah).visibility =
                        if (it.surah == 1 || it.surah == 9)
                            View.GONE else View.VISIBLE
                    findViewById<TextView>(R.id.details).text = revelation(it.pronunciation) +
                            "   |   ${NumberFormat.getInstance(
                                Locale(Application(context).language)
                            ).format(it.ayat)}" +
                            "  " + resources.getString(R.string.verses)

                }
            }
        }
    }

    private fun revelation(revelation: String): String {
        return if (revelation == "Meccan")
            context.resources.getString(R.string.meccan)
        else context.resources.getString(R.string.medinan)
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].id
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun maintainClicks(share: ImageView?, bookmark: ImageView?, play: ImageView?, model: QPModel) {
        share?.setOnClickListener { v->
            var text = "${context.resources.getString(R.string.surah)}: ${model.surah}, " +
                    "${context.resources.getString(R.string.ayat)}: ${model.ayat}\n\n"
            text += model.arabic
            text += "\n\n${context.resources.getString(R.string.meaning)} " +
                    ":  "+model.translation

//            val sendIntent = Intent()
//            sendIntent.action = Intent.ACTION_SEND
//            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
//            sendIntent.type = "text/plain"
//            val shareIntent = Intent.createChooser(sendIntent, null)
//            context.startActivity(shareIntent)

            shareSheet.share(model.arabic, model.translation,
                "${model.name}, ayah ${model.ayat}", text)
        }

        play?.setOnClickListener { _->
            AudioProcess(context as Activity).play(model.surah, model.ayat)
        }

        bookmark?.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                if (bookmarkDB.onRead(model.id))
                    R.drawable.ic_baseline_bookmark_24
                else R.drawable.ic_baseline_bookmark_border_24,
                null
            )
        )

        bookmark?.setOnClickListener { _ ->
            if (bookmarkDB.onRead(model.id)) {
                bookmarkDB.onDelete(model.id)
            } else bookmarkDB.onWrite(model.id)
            bookmark.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    if (bookmarkDB.onRead(model.id))
                        R.drawable.ic_baseline_bookmark_24
                    else R.drawable.ic_baseline_bookmark_border_24,
                    null
                )
            )
        }
    }

    private val numberFormat: NumberFormat =
        NumberFormat.getInstance(Locale(Application(context).language))
}