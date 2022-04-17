package com.code.apppraytime.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.R
import com.code.apppraytime.data.model.QAModel
import com.code.apppraytime.classes.AudioProcess
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
import kotlin.collections.ArrayList

class SurahAyatAdapter(val context: Context, val data: ArrayList<QAModel>):
    RecyclerView.Adapter<SurahAyatAdapter.ViewHolder>() {

    private var reading = -1
    private val exporter = HtmlExporter()
    private val application = Application(context)
    private val bookmarkDB = BookmarkHelper(context)
    private val shareSheet by lazy { ShareSheet(context) }

    inner class ViewHolder(val view: View, type: Int): RecyclerView.ViewHolder(view) {

        lateinit var play: ImageView
        lateinit var share: ImageView
        lateinit var bookmark: ImageView

        init {
            if (type > 0) {
                play = view.findViewById(R.id.play)
                share = view.findViewById(R.id.share)
                bookmark = view.findViewById(R.id.bookmark)
                play.clipToOutline = true
                share.clipToOutline = true
                bookmark.clipToOutline = true
            }
        }

        fun bind(model: QAModel) {
            val pron = view.findViewById<TextView>(R.id.pron)
            val arabic = view.findViewById<TextView>(R.id.arabic)
            val ayatNo = view.findViewById<TextView>(R.id.ayat_no)
            val translation = view.findViewById<TextView>(R.id.translation)
//            if (it.reading) {
//                itemView.findViewById<LinearLayout>(R.id.main_layout)
//                    .background = ResourcesCompat.getDrawable(
//                    context.resources, R.drawable.reading_background, context.theme)
//            } else itemView.findViewById<LinearLayout>(R.id.main_layout).background = null
            itemView.findViewById<LinearLayout>(R.id.main_layout).background = null
            ayatNo.text = numberFormat.format(model.ayat)             //it.ayat.toString()
            arabic.text = if (application.tajweed)
                html(model.arabic) else model.arabic

            if (Application(context).transliteration) {
                pron.text = model.pronunciation
                pron.visibility = View.VISIBLE
                translation.visibility = View.VISIBLE
                translation.text = textToHtml(model.translation)
                pron.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    Application(context).transliterationFontSize)
                translation.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    Application(context).translationFontSize)
            } else {
                translation.visibility = View.GONE
                pron.visibility = View.VISIBLE
                pron.text = textToHtml(model.translation)
                pron.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    Application(context).translationFontSize
                )
            }

            maintainClicks(play, share, bookmark, model)
            arabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                Application(context).arabicFontSize)
        }
    }

    private fun html(text: String): Spanned {
        val results: MutableList<Result> = java.util.ArrayList()
        TajweedRule.MADANI_RULES.forEach {
            results.addAll(it.rule.checkAyah(text))
        }
        ResultUtil.INSTANCE.sort(results)
        return HtmlCompat.fromHtml(exporter.export(text, results), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahAyatAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    if (viewType == 0)
                        R.layout.layout_ayat_header
                    else
                        R.layout.layout_ayat_reading, parent, false
                ), viewType
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SurahAyatAdapter.ViewHolder, position: Int) {
        data[position].let {
            if (getItemViewType(position) > 0) {
                holder.bind(it)
            } else {
                holder.itemView.run {
                    findViewById<TextView>(R.id.name).text = it.translation
                    findViewById<TextView>(R.id.meaning).text = surahMeaning[it.id-1]
                    findViewById<ImageView>(R.id.bismillah).visibility =
                        if (it.id == 1 || it.id == 9)
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

    private fun textToHtml(it: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
        else Html.fromHtml(it)
    }

    private fun maintainClicks(
        play: ImageView?, share: ImageView?,
        bookmark: ImageView?, model: QAModel
    ) {
        share?.setOnClickListener { _->
            var text = "${context.resources.getString(R.string.surah)}: ${data[0].translation}, " +
                    "${context.resources.getString(R.string.ayat)}: ${model.ayat}\n\n"
            text += model.arabic
            text += "\n\n${context.resources.getString(R.string.meaning)} " +
                    ":  "+model.translation
//
//            val sendIntent = Intent()
//            sendIntent.action = Intent.ACTION_SEND
//            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
//            sendIntent.type = "text/plain"
//            val shareIntent = Intent.createChooser(sendIntent, null)
//            context.startActivity(shareIntent)
            shareSheet.share(model.arabic, model.translation,
                "${data[0].translation}, ayah ${model.ayat}", text)
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

        play?.setOnClickListener { _->
            AudioProcess(context as Activity).play(data[0].id, model.ayat)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return data.size
    }

//    fun read(pos: Int) {
//        data[pos] = data[pos].apply { reading = true }
//        if (reading > 0) {
//            data[reading] = data[reading].apply { reading = false }
//        }
//        notifyItemChanged(pos)
//        notifyItemChanged(reading)
//        reading = pos
//    }

    private val numberFormat: NumberFormat =
        NumberFormat.getInstance(Locale(Application(context).language))
}