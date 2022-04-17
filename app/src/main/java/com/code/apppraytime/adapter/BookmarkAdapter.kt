package com.code.apppraytime.adapter

import android.content.Context
import android.text.Spanned
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.R
import com.code.apppraytime.data.model.QPModel
import com.code.apppraytime.interfaces.Bookmark
import com.code.apppraytime.screen.SurahActivity
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.BookmarkHelper
import com.code.apppraytime.external.ShareSheet
import com.code.apppraytime.tajweed.exporter.Exporter
import com.code.apppraytime.tajweed.exporter.HtmlExporter
import com.code.apppraytime.tajweed.model.Result
import com.code.apppraytime.tajweed.model.ResultUtil
import com.code.apppraytime.tajweed.model.TajweedRule

class BookmarkAdapter(
    val context: Context, val data: ArrayList<QPModel>,
    private val bookmarkInterface: Bookmark
    ) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    private val application = Application(context)
    private val bookmarkDB = BookmarkHelper(context)
    private val shareSheet by lazy { ShareSheet(context) }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var pron: TextView? = null
        var play: ImageView? = null
        var share: ImageView? = null
        var arabic: TextView? = null
        var ayatNo: TextView? = null
        var bookmark: ImageView? = null
        var surahName: TextView? = null
        var translation: TextView? = null

        init {
            pron = view.findViewById(R.id.pron)
            play = view.findViewById(R.id.play)
            share = view.findViewById(R.id.share)
            arabic = view.findViewById(R.id.arabic)
            ayatNo = view.findViewById(R.id.ayat_no)
            bookmark = view.findViewById(R.id.bookmark)
            surahName = view.findViewById(R.id.surah_name)
            translation = view.findViewById(R.id.translation)

            play?.clipToOutline = true
            share?.clipToOutline = true
            bookmark?.clipToOutline = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    R.layout.layout_ayat,
                    parent, false
                )
        )
    }

    private fun html(text: String): Spanned {
        val exporter: Exporter = HtmlExporter()
        val results: MutableList<Result> = java.util.ArrayList()
        TajweedRule.MADANI_RULES.forEach {
            results.addAll(it.rule.checkAyah(text))
        }
        ResultUtil.INSTANCE.sort(results)
        return HtmlCompat.fromHtml(
            exporter.export(text, results),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    override fun onBindViewHolder(holder: BookmarkAdapter.ViewHolder, position: Int) {
        data[position].let {
            holder.run {
                ayatNo?.text = it.ayat.toString()
                arabic?.text = if (application.tajweed)
                    html(it.arabic) else it.arabic
                surahName?.text = it.name

                if (Application(context).transliteration) {
                    pron?.text = it.pronunciation
                    pron?.visibility = View.VISIBLE
                    translation?.visibility = View.VISIBLE
                    translation?.text = it.translation
                } else {
                    translation?.visibility = View.GONE
                    pron?.visibility = View.VISIBLE
                    pron?.text = it.translation
                }

                share?.setOnClickListener { _->
                    var text = "Surah: ${it.surah}" +
                            ", Ayat: ${it.ayat}\n\n"
                    text += it.arabic
                    text += "\n\nঅর্থ :  "+it.translation

                    shareSheet.share(it.arabic, it.translation,
                        "${it.name}, ayah ${it.ayat}", text)
                }

                bookmark?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_baseline_bookmark_24,
                        null
                    )
                )

                bookmark?.setOnClickListener { _->
                    bookmarkDB.onDelete(it.id)
                    bookmarkInterface.removed(position)

                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount - position)
                }

                holder.itemView.setOnClickListener { _->
                    SurahActivity.launch(context, it.surah, it.ayat)
                }

                Application(context).let {
                    arabic?.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        it.arabicFontSize
                    )
                    pron?.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        it.transliterationFontSize
                    )
                    translation?.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        it.translationFontSize
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}