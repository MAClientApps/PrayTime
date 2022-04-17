package com.code.apppraytime.screen.layout

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.code.apppraytime.databinding.FragmentSurahAyatBinding
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.code.apppraytime.adapter.SurahAyatAdapter
import com.code.apppraytime.data.model.QAModel
import com.code.apppraytime.data.QuranData
import com.code.apppraytime.constant.App
import com.code.apppraytime.shared.LastRead
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.utils.KeyboardUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SurahAyat : Fragment() {

    //
    private var position: Int = 0
    private var ayat: Int = 0
    private var scroll: Boolean = false

    private var search = ""
    private val data = ArrayList<QAModel>()
    private lateinit var smoothScroller: RecyclerView.SmoothScroller
    private var ayatFollower: BroadcastReceiver? = null
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapterSurah: SurahAyatAdapter
    private var _binding: FragmentSurahAyatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentSurahAyatBinding.inflate(inflater, container, false)
        binding.searchIcon.clipToOutline = true
        arguments?.let {
            position = it.getInt("position")
            ayat = it.getInt("ayat")
            scroll = it.getBoolean("scroll")
        }

        layoutManager = LinearLayoutManager(requireContext())

        smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        data.addAll(QuranData(requireContext()).getSurah(position))

        adapterSurah = SurahAyatAdapter(requireContext(), data)

        IndexHelper(requireContext()).readSurahX(position)?.let {
            binding.ayatRecycler.layoutManager = layoutManager
            binding.ayatRecycler.adapter = adapterSurah
            if (scroll) binding.ayatRecycler.scrollToPosition(ayat)
        }

        click()

        return binding.root
    }

    private fun click() {
        binding.searchIcon.setOnClickListener {
            binding.searchText.text.toString().let { e->
                search = if (e.isNotEmpty()) {
                    try {
                        filter(e.toInt())
                    } catch (ex: Exception) {
                        filter(e)
                    }
                    e
                } else {
                    filter("")
                    ""
                }
            }
            closeKeyboard(binding.searchText)
        }

        binding.searchText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = binding.searchText.text.toString()
                search = if (key.isNotEmpty()) {
                    try {
                        filter(key.toInt())
                    } catch (ex: Exception) {
                        filter(key)
                    }
                    key
                } else {
                    filter(0)
                    "0"
                }
                closeKeyboard(binding.searchText)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun filter(pos: Int) {
        val l = data.size
        data.clear()
        adapterSurah.notifyItemRangeRemoved(0, l)
        data.addAll(QuranData(requireContext()).getSurah(position))
        adapterSurah.notifyItemRangeInserted(0, data.size)
        if (pos < data.size)
            binding.ayatRecycler.scrollToPosition(pos)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filter(filter: String) {
        val header = data[0]
        adapterSurah.notifyItemRangeRemoved(0, data.size)
        data.clear()
        CoroutineScope(Dispatchers.Default).launch {
            val a = QuranData(requireContext()).getSurah(position).filter {
                it.translation.lowercase().contains(filter.lowercase())
            } as ArrayList<QAModel>
            if (header.translation.lowercase().contains(filter.lowercase()) && a.size > 0)
                a.removeAt(0)
            data.add(header)
            a.forEach {
                val temp = it.translation
                val start = temp.lowercase(Locale.getDefault())
                    .indexOf(filter.lowercase(Locale.getDefault()))
                val translation = "${temp.substring(0, start)}<b><font color=#F44336>" +
                        "${temp.substring(start, start+filter.length)}</font></b>" +
                        temp.substring(start+filter.length)
                data.add(
                    QAModel(
                        id = it.id,
                        ayat = it.ayat,
                        arabic = it.arabic,
                        pronunciation = it.pronunciation,
                        translation = translation
                    )
                )
            }
            activity?.runOnUiThread {
                adapterSurah.notifyDataSetChanged()
                Toast.makeText(context,
                    "${data.size} Search result found."
                    , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun closeKeyboard(edit: EditText?) {
        edit?.let {
            it.clearFocus()
            KeyboardUtils.hideKeyboard(it)
        }
    }

    override fun onResume() {
        super.onResume()
        ayatFollower = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    binding.appBar.setExpanded(false, true)
                    smoothScroller.targetPosition = intent.getIntExtra("AYAT", 0)+1
                    layoutManager.startSmoothScroll(smoothScroller)
//                    adapterSurah?.read(intent.getIntExtra("AYAT", 0)+1)
                }
            }
        }
        activity?.registerReceiver(ayatFollower, IntentFilter(App.SURAH+position))
    }

    override fun onPause() {
        activity?.unregisterReceiver(ayatFollower)
        if (LastRead(requireContext()).surahNo == position) {
            LastRead(requireContext()).ayatNo =
                layoutManager.findFirstCompletelyVisibleItemPosition().let {
                    if (it < 0)
                        layoutManager.findFirstVisibleItemPosition()
                    else it
                }
        }
        super.onPause()
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}
