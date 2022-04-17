package com.code.apppraytime.views.calander

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.views.calander.HorizontalCalendarAdapter.MyViewHolder
import android.widget.TextView
import android.widget.LinearLayout
import com.code.apppraytime.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.view.View
import java.text.SimpleDateFormat
import java.util.ArrayList
import kotlin.math.roundToInt

class HorizontalCalendarAdapter(
    private val list: ArrayList<HorizontalCalendarModel>,
    private val mCtx: Context) : RecyclerView.Adapter<MyViewHolder>() {

    private var onCalendarListener: CalendarView.OnCalendarListener? = null

    fun setOnCalendarListener(onCalendarListener: CalendarView.OnCalendarListener?) {
        this.onCalendarListener = onCalendarListener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView = view.findViewById(R.id.date)
        var month: TextView = view.findViewById(R.id.month)
        var day: TextView = view.findViewById(R.id.day)
        var parent: LinearLayout = view.findViewById(R.id.parent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.horizontal_calendar_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        val display = (mCtx as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        holder.parent.minimumWidth = (width / 7).toFloat().roundToInt()
        val sdf = SimpleDateFormat("dd MMM EEE")
        val sdf1 = SimpleDateFormat("dd-MM-yyyy")
        holder.date.text = sdf.format(model.getTimeinmilli()).split(" ").toTypedArray()[0]
        holder.month.text = sdf.format(model.getTimeinmilli()).split(" ").toTypedArray()[1]
        holder.day.text = sdf.format(model.getTimeinmilli()).split(" ").toTypedArray()[2]
        if (model.getStatus() == 0) {
            holder.date.setTextColor(mCtx.getColor(R.color.grey_600))
            holder.month.setTextColor(mCtx.getColor(R.color.grey_600))
            holder.day.setTextColor(mCtx.getColor(R.color.grey_600))
            holder.parent.setBackgroundColor(Color.TRANSPARENT)
        } else {
            holder.date.setTextColor(mCtx.getColor(R.color.textColorLight))
            holder.month.setTextColor(mCtx.getColor(R.color.textColorLight))
            holder.day.setTextColor(mCtx.getColor(R.color.textColorLight))
            holder.parent.setBackgroundResource(R.drawable.color_status_1)
        }
        holder.parent.setOnClickListener { onCalendarListener!!.onDateSelected(sdf1.format(model.getTimeinmilli())) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}