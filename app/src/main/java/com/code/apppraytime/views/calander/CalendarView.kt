package com.code.apppraytime.views.calander

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.code.apppraytime.R
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("CustomViewStyleable", "SetTextI18n", "SimpleDateFormat")
class CalendarView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var attributeSet: AttributeSet? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: HorizontalCalendarAdapter? = null
    private lateinit var list: ArrayList<HorizontalCalendarModel>

    init {
        val view = inflate(getContext(), R.layout.horizontal_calendar, null)
        val textView = view.findViewById<TextView>(R.id.text)
        recyclerView = view.findViewById(R.id.re)
        view.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (attributeSet != null) {
            val attribute =
                context.obtainStyledAttributes(attributeSet, R.styleable.HorizontalCalendarView)
            textView.text = attribute.getString(R.styleable.HorizontalCalendarView_text)
            attribute.recycle()
        } else {
            textView.text = "No Text Provided"
        }
        textView.visibility = GONE
        addView(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUpCalendar(
        start: Long,
        end: Long,
        dates: ArrayList<String?>,
        onCalendarListener: OnCalendarListener?) {
        val c = Calendar.getInstance()
        c.timeInMillis = start
        list = ArrayList()
        val today = Tools.getTimeInMillis(Tools.formattedDateToday)
        var current = start
        var i = 0
        var pos = 0
        while (current < end) {
            val c1 = Calendar.getInstance()
            c1.timeInMillis = start
            c1.add(Calendar.DATE, i)
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            if (sdf.format(c1.timeInMillis).equals(sdf.format(today), ignoreCase = true)) {
                pos = i
                Log.d("Position", pos.toString() + "")
            }
            val model = HorizontalCalendarModel(c1.timeInMillis)
            if (dates.contains(sdf.format(c1.timeInMillis))) {
                model.setStatus(1)
            }
            list.add(model)
            current = c1.timeInMillis
            i++
            Log.d("Setting data", sdf.format(c1.timeInMillis))
        }
        adapter = HorizontalCalendarAdapter(list, context)
        adapter!!.setOnCalendarListener(onCalendarListener)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        //        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,7,RecyclerView.VERTICAL,false);
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        (context as Activity).runOnUiThread {
            recyclerView!!.layoutManager = layoutManager
            recyclerView!!.adapter = adapter
            adapter!!.notifyDataSetChanged()
            recyclerView!!.scrollToPosition(pos-1)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(start: Long, end: Long, dates: ArrayList<String?>) {
        list.clear()
        var current = start
        var i = 0
        while (current < end) {
            val c1 = Calendar.getInstance()
            c1.timeInMillis = start
            c1.add(Calendar.DATE, i)
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val model = HorizontalCalendarModel(c1.timeInMillis)
            if (dates.contains(sdf.format(c1.timeInMillis))) {
                model.setStatus(1)
            }
            list.add(model)
            current = c1.timeInMillis
            i++
            Log.d("Setting data", sdf.format(c1.timeInMillis))
        }
        (context as Activity).runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }

    interface OnCalendarListener {
        fun onDateSelected(date: String?)
    }
}