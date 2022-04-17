package com.code.apppraytime.external

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.code.apppraytime.R
import com.code.apppraytime.adapter.SearchAdapter
import com.code.apppraytime.model.SearchModel
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.sql.TranslationHelper
import com.code.apppraytime.utils.KeyboardUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class Search(val context: Activity) {

    private var search = ""
    private var option: RadioGroup? = null
    private var adapter: SearchAdapter? = null
    private var searching: ProgressBar? = null
    private val data = ArrayList<SearchModel>()
    private var recyclerView: RecyclerView? = null

    fun searchSheet() {
        val searchSheetDialog = BottomSheetDialog(context, if (Application(context).darkTheme)
            R.style.bottomSheetDark else R.style.bottomSheet)
        searchSheetDialog.setContentView(R.layout.search_sheet)

        searchSheetDialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            option = bottomSheetDialog.findViewById(R.id.filter_group)
            recyclerView = bottomSheetDialog.findViewById(R.id.search_recycler)
            searching = bottomSheetDialog.findViewById(R.id.searching)
//            bottomSheetDialog.findViewById<BlurView>(R.id.close)?.clipToOutline = true
//            bottomSheetDialog.findViewById<BlurView>(R.id.blurView)?.clipToOutline = true
            bottomSheetDialog.findViewById<ImageView>(R.id.search_icon)?.clipToOutline = true

            adapter = SearchAdapter(context, data)
            bottomSheetDialog.findViewById<RecyclerView>(R.id.search_recycler)
                ?.let { r->
                    r.layoutManager = LinearLayoutManager(context)
                    r.adapter = adapter
                }

            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                val layoutParams = layout.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                layout.layoutParams = layoutParams

                behaviour.setBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            stateChange(newState, behaviour)
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            Log.e("TAG", "Slide")
                        }
                    }
                )

                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }

            searchSheetDialog.findViewById<EditText>(R.id.search_text)
                ?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val key = searchSheetDialog.findViewById<EditText>(R.id.search_text)!!.text.toString()
                        search = if (key.isNotEmpty()) {
                            try {
                                filter(key.toInt())
                            } catch (ex: Exception) {
                                filter(key)
                            }
                            key
                        } else {
                            filter("")
                            ""
                        }
                        closeKeyboard(searchSheetDialog.findViewById(R.id.search_text))
                        return@OnEditorActionListener true
                    }
                    false
                })

            buttonClick(searchSheetDialog)

            filter(search)

            searchSheetDialog.findViewById<ImageView>(R.id.closet)
                ?.let { close->
                    close.clipToOutline = true
                    close.setOnClickListener { searchSheetDialog.dismiss() }
                }
        }
        searchSheetDialog.show()
    }

    private fun stateChange(newState: Int, behaviour: BottomSheetBehavior<View>) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            (behaviour as BottomSheetBehavior<*>).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun buttonClick(searchSheetDialog: BottomSheetDialog) {
        searchSheetDialog.findViewById<ImageView>(R.id.search_icon)
            ?.setOnClickListener {
                searchSheetDialog.findViewById<EditText>(R.id.search_text)
                    ?.text.toString().let { e->
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
                closeKeyboard(searchSheetDialog.findViewById(R.id.search_text))
            }

        option?.setOnCheckedChangeListener { _, _ ->
            try {
                filter(search.toInt())
            } catch (ex: Exception) {
                filter(search)
            }
        }
    }

    private fun filter(no: Int) {
        adapter?.notifyDataSetChanged()
        searching?.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            data.clear()
            when(option?.checkedRadioButtonId) {
                R.id.surah -> {
                    val a = IndexHelper(context).readSurahAll().filter {
                        it.id == no
                    }
                    a.forEach {
                        data.add(
                            SearchModel(
                                type = SearchAdapter.SURAH,
                                pos = it.id,
                                name = it.name,
                                revelation = it.revelation,
                                verse = it.verse,
                                nameAr = it.arabic
                            )
                        )
                    }
                }
                else -> {
                    val c = TranslationHelper(context, "latin").readAll()
                    val a = TranslationHelper(
                        context, if (Application(context).arabic) "utsmani" else "indopak"
                    ).readAll()
                    val b = TranslationHelper(context, Application(context).translation).readAll()
                    val temp = ArrayList<SearchModel>()
                    for (x in 0 until a.size) {
                        temp.add(
                            SearchModel(
                                type = SearchAdapter.AYAT,
                                pos = a[x].id,
                                surah = a[x].surah,
                                ayat = a[x].ayah,
                                arabic = a[x].text,
                                pronunciation = c[x].text,
                                translation = b[x].text
                            )
                        )
                    }

                    data.addAll(
                        temp.filter {
                            it.ayat == no
                        }
                    )
                }
            }
            context.runOnUiThread {
                searching?.visibility = View.GONE
                adapter?.notifyDataSetChanged()
                Toast.makeText(context,
                    "${data.size} Search result found."
                    , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filter(filter: String) {
        adapter?.notifyDataSetChanged()
        searching?.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            data.clear()
            when(option?.checkedRadioButtonId) {
                R.id.surah -> {
                    val a = IndexHelper(context).readSurahAll().filter {
                        it.name.contains(filter)
                    }
                    a.forEach {
                        data.add(
                            SearchModel(
                                type = SearchAdapter.SURAH,
                                pos = it.id,
                                name = it.name,
                                revelation = it.revelation,
                                verse = it.verse,
                                nameAr = it.arabic
                            )
                        )
                    }
                }

                R.id.ayat -> {
                    val c = TranslationHelper(context, "latin").readAll()
                    val a = TranslationHelper(
                        context, if (Application(context).arabic) "utsmani" else "indopak"
                    ).readAll()
                    val b = TranslationHelper(context, Application(context).translation).readAll()
                    val temp = ArrayList<SearchModel>()
                    for (x in 0 until a.size) {
                        temp.add(
                            SearchModel(
                                type = SearchAdapter.AYAT,
                                pos = a[x].id,
                                surah = a[x].surah,
                                ayat = a[x].ayah,
                                arabic = a[x].text,
                                pronunciation = c[x].text,
                                translation = b[x].text
                            )
                        )
                    }

                    temp.filter {
                        it.arabic.lowercase().contains(filter.lowercase())
                    }.forEach {
                        val ar = it.arabic.lowercase()
                        val start = ar.indexOf(filter.lowercase())
                        val done = "${ar.substring(0, start)}<b><font color=#2979FF>" +
                                "$filter</font></b>${ar.substring(start+filter.length)}"
                        data.add(
                            SearchModel(
                                type = SearchAdapter.AYAT,
                                pos = it.pos,
                                surah = it.surah,
                                ayat = it.ayat,
                                arabic = done,
                                pronunciation = it.pronunciation,
                                translation = it.translation
                            )
                        )
                    }
                }

                R.id.meaning -> {
                    val c = TranslationHelper(context, "latin").readAll()
                    val a = TranslationHelper(
                        context, if (Application(context).arabic) "utsmani" else "indopak"
                    ).readAll()
                    val b = TranslationHelper(context, Application(context).translation).readAll()
                    val temp = ArrayList<SearchModel>()
                    for (x in 0 until a.size) {
                        temp.add(
                            SearchModel(
                                type = SearchAdapter.AYAT,
                                pos = a[x].id,
                                surah = a[x].surah,
                                ayat = a[x].ayah,
                                arabic = a[x].text,
                                pronunciation = c[x].text,
                                translation = b[x].text
                            )
                        )
                    }

                    temp.filter {
                        it.translation.lowercase().contains(filter.lowercase())
                    }.forEach {
                        val tr = it.translation.lowercase()
                        val start = tr.indexOf(filter.lowercase())
                        val done = "${tr.substring(0, start)}<b><font color=#2979FF>" +
                                "$filter</font></b>${tr.substring(start+filter.length)}"
                        data.add(
                            SearchModel(
                                type = SearchAdapter.AYAT,
                                pos = it.pos,
                                surah = it.surah,
                                ayat = it.ayat,
                                arabic = it.arabic,
                                pronunciation = it.pronunciation,
                                translation = done
                            )
                        )
                    }
                }

//                R.id.meaning -> {
//                    val a = QuranHelper(context).readData().filter {
//                        when(Application(context).translation) {
//                            Application.TAISIRUL -> it.terjemahan
//                            Application.MUHIUDDIN -> it.jalalayn
//                            else -> it.englishT.lowercase(Locale.getDefault())
//                        }.contains(filter.lowercase())
//                    }
//                    a.forEach {
//                        val temp = when(Application(context).translation) {
//                            Application.TAISIRUL -> it.terjemahan
//                            Application.MUHIUDDIN -> it.jalalayn
//                            else -> it.englishT
//                        }
//                        val start = temp.lowercase(Locale.getDefault())
//                            .indexOf(filter.lowercase(Locale.getDefault()))
//                        val translation = "${temp.substring(0, start)}<b><font color=#2979FF>" +
//                                "${temp.substring(start, start+filter.length)}</font></b>${temp.substring(start+filter.length)}"
//                        data.add(
//                            SearchModel(
//                                type = SearchAdapter.AYAT,
//                                pos = it.pos,
//                                surah = it.surah,
//                                ayat = it.ayat,
//                                indopak = it.indopak,
//                                utsmani = it.utsmani,
//                                jalalayn = it.jalalayn,
//                                latin = it.latin,
//                                terjemahan = it.terjemahan,
//                                englishPro = it.englishPro,
//                                trans = translation
//                            )
//                        )
//                    }
//                }
            }
            context.runOnUiThread {
                searching?.visibility = View.GONE
                adapter?.notifyDataSetChanged()
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
}