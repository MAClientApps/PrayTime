package com.code.apppraytime.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.R
import com.code.apppraytime.model.JuzModel
import com.code.apppraytime.screen.ParaActivity
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.sql.TranslationHelper
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ParaListAdapter(val context: Context, val data: ArrayList<JuzModel>):
    RecyclerView.Adapter<ParaListAdapter.ViewHolder>() {

    private val application = Application(context)
    private val layout = if (application.layoutStyle == 0)
        R.layout.layout_para_2 else R.layout.layout_para
    private val translation = TranslationHelper(context, "en_sahih")

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val from: TextView = view.findViewById(R.id.from)
        val count: TextView = view.findViewById(R.id.count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParaListAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(layout, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ParaListAdapter.ViewHolder, position: Int) {
        data[position].let {
//            val quran = QuranHelper(context).readAyatNo(it.start)!!
            val index = IndexHelper(context).readSurahX(translation.readAt(it.start)!!.surah)!!
            holder.run {
                count.text = numberFormat.format(it.id) //"${it.paraNo}"
                name.text = "${context.resources.getString(R.string.para)} " +
                        "${numberFormat.format(it.id)} -> ${numberFormat.format(
                            it.end+1-it.start)} " +
                        context.resources.getString(R.string.verses)
                from.text = "${context.resources.getString(R.string.starts_at)}: " +
                        "${index.name}," +
                        " "+context.resources.getString(R.string.verses)+" ${translation.readAt(it.start)!!.ayah}"

                holder.itemView.setOnClickListener { _->
                    ParaActivity.launch(context, it.id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private val numberFormat: NumberFormat =
        NumberFormat.getInstance(Locale(Application(context).language))
}