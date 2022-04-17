package com.code.apppraytime.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.R
import com.code.apppraytime.model.Surah
import com.code.apppraytime.screen.SurahActivity
import com.code.apppraytime.shared.Application
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SurahListAdapter(val context: Context, val data: ArrayList<Surah>):
    RecyclerView.Adapter<SurahListAdapter.ViewHolder>() {

    private val application = Application(context)
    val layout = if (application.layoutStyle == 0)
        R.layout.layout_surah_name_2 else R.layout.layout_surah_name

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(model: Surah) {
            view.findViewById<TextView>(R.id.count).text = numberFormat.format(model.id)
            view.findViewById<TextView>(R.id.name).text = model.name
            view.findViewById<TextView>(R.id.from).text = "${revelation(model.revelation)}   -   " +
                    "${numberFormat.format(model.verse)}  " + context.resources.getString(R.string.verses)
            view.findViewById<TextView>(R.id.arabic).text = model.arabic

            view.setOnClickListener {
                SurahActivity.launch(context, model.id, 0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            SurahListAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SurahListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    private fun revelation(revelation: String): String {
        return if (revelation == "Meccan")
            context.resources.getString(R.string.meccan)
        else context.resources.getString(R.string.medinan)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private val numberFormat: NumberFormat =
        NumberFormat.getInstance(Locale(Application(context).language))
}