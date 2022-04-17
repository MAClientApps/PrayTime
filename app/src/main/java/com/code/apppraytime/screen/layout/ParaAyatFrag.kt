package com.code.apppraytime.screen.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.apppraytime.adapter.ParaAyatAdapter
import com.code.apppraytime.data.QuranData
import com.code.apppraytime.databinding.FragmentParaAyatBinding
import com.code.apppraytime.model.JuzModel
import com.code.apppraytime.sql.IndexHelper

class ParaAyatFrag(private val position: Int) : Fragment() {

    private var search = ""
    private var adapterSurah: ParaAyatAdapter? = null
    private var binding: FragmentParaAyatBinding? = null
    private lateinit var index: JuzModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentParaAyatBinding.inflate(inflater, container, false)
        index = IndexHelper(requireContext()).readParaX(position)!!

//        initiate()

        IndexHelper(requireContext()).readParaX(position)?.let {
            adapterSurah = ParaAyatAdapter(
                requireContext(), QuranData(requireContext()).getPara(position)
            )
            binding?.ayatRecycler?.layoutManager = LinearLayoutManager(requireContext())
            binding?.ayatRecycler?.adapter = adapterSurah
        }

//        binding?.searchText?.setOnEditorActionListener(
//            TextView.OnEditorActionListener { _, actionId, _ ->
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    val key = binding?.searchText?.text.toString()
//                    search = if (key.isNotEmpty()) {
//                        try {
//                            filter(key.toInt())
//                        } catch (ex: Exception) {
//                            Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
//                        }
//                        key
//                    } else {
//                        clearAll()
//                        ""
//                    }
//                    closeKeyboard(binding?.searchText)
//                    return@OnEditorActionListener true
//                }
//                false
//            })
//
//        binding?.searchIcon?.setOnClickListener {
//            searchIconClick()
//        }

        return binding?.root
    }

//    val type: Int,
//    val pos: Int,
//    val surah: Int,
//    val ayat: Int,
//    val indopak: String,
//    val utsmani: String,
//    val jalalayn: String,
//    val latin: String,
//    val terjemahan: String,
//    val englishPro: String,
//    val englishT: String,
//    val name: String,
//    val meaning: String,
//    val details: String

//    private fun initiate() {
//        CoroutineScope(Dispatchers.Default).launch {
//
//
//
//            surahHelper = SurahHelper(requireContext())
//            quranHelper = QuranHelper(requireContext())
//            QuranHelper(requireContext()).readAyatXtoY(
//                index.start, index.end
//            ).forEach {
//                if (it.ayat == 1)
//                    data.add(modelExchange(it, true))
//                data.add(modelExchange(it, false))
//            }
//            adapterSurah = ParaAyatAdapter(
//                requireContext(), data
//            )
//            activity?.runOnUiThread {
//                binding?.ayatRecycler?.layoutManager = LinearLayoutManager(requireContext())
//                binding?.ayatRecycler?.adapter = adapterSurah
//            }
//        }
//    }
//
//    private fun clearAll() {
//        data.clear()
//        adapterSurah?.notifyDataSetChanged()
//        CoroutineScope(Dispatchers.Default).launch {
//            QuranHelper(requireContext()).readAyatXtoY(
//                index.start, index.end
//            ).forEach {
//                if (it.ayat == 1)
//                    data.add(modelExchange(it, true))
//                data.add(modelExchange(it, false))
//            }
//            activity?.runOnUiThread {
//                adapterSurah?.notifyDataSetChanged()
//            }
//        }
//    }
//
//    private fun searchIconClick() {
//        binding?.searchText?.text.toString().let { e->
//            search = if (e.isNotEmpty()) {
//                try {
//                    filter(e.toInt())
//                } catch (ex: Exception) {
//                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
//                }
//                e
//            } else {
//                data.clear()
//                adapterSurah?.notifyDataSetChanged()
//                CoroutineScope(Dispatchers.Default).launch {
//                    QuranHelper(requireContext()).readAyatXtoY(
//                        index.start, index.end
//                    ).forEach {
//                        if (it.ayat == 1)
//                            data.add(modelExchange(it, true))
//                        data.add(modelExchange(it, false))
//                    }
//                    activity?.runOnUiThread {
//                        adapterSurah?.notifyDataSetChanged()
//                    }
//                }
//                ""
//            }
//        }
//        closeKeyboard(binding?.searchText)
//    }
//
//    private fun filter(pos: Int) {
//        data.clear()
//        adapterSurah?.notifyDataSetChanged()
//        QuranHelper(requireContext()).readAyatXtoY(
//            index.start, index.end
//        ).forEach {
//            if (it.ayat == 1)
//                data.add(modelExchange(it, true))
//            data.add(modelExchange(it, false))
//        }
//        adapterSurah?.notifyDataSetChanged()
//        if (pos < data.size) {
//            binding?.ayatRecycler?.scrollToPosition(pos)
//        }
//    }
//
//    private fun closeKeyboard(edit: EditText?) {
//        edit?.let {
//            it.clearFocus()
//            KeyboardUtils.hideKeyboard(it)
//        }
//    }
//
//    private fun modelExchange(temp: Quran, name: Boolean): ParaAyat {
//        if (name) {
//            val s = surahHelper!!.readDataAt(quranHelper!!.readAyatNo(temp.pos)!!.surah)
//            return ParaAyat(
//                type = 1,
//                pos = temp.pos,
//                surah = temp.surah,
//                ayat = temp.ayat,
//                indopak = temp.indopak,
//                utsmani = temp.utsmani,
//                jalalayn = temp.jalalayn,
//                latin = temp.latin,
//                terjemahan = temp.terjemahan,
//                englishPro = temp.englishPro,
//                englishT = temp.englishT,
//                name = s!!.name,
//                meaning = Name().data()[temp.surah-1],
//                details = "${s.revelation}   |   ${s.verse} VERSES"
//            )
//        } else {
//            return ParaAyat(
//                type = 0,
//                pos = temp.pos,
//                surah = temp.surah,
//                ayat = temp.ayat,
//                indopak = temp.indopak,
//                utsmani = temp.utsmani,
//                jalalayn = temp.jalalayn,
//                latin = temp.latin,
//                terjemahan = temp.terjemahan,
//                englishPro = temp.englishPro,
//                englishT = temp.englishT,
//                name = "",
//                meaning = "",
//                details = ""
//            )
//        }
//    }

    override fun onDetach() {
        super.onDetach()
        binding = null
    }
}
