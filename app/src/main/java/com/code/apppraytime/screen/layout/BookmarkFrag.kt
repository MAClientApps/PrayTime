package com.code.apppraytime.screen.layout

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.apppraytime.adapter.BookmarkAdapter
import com.code.apppraytime.data.model.QPModel
import com.code.apppraytime.data.QuranData
import com.code.apppraytime.databinding.FragmentBookmarkBinding
import com.code.apppraytime.interfaces.Bookmark
import com.code.apppraytime.sql.BookmarkHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkFrag : Fragment(), Bookmark {

    private val data = ArrayList<QPModel>()
    private lateinit var adapter: BookmarkAdapter
    private var binding: FragmentBookmarkBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        adapter = BookmarkAdapter(requireContext(), data, this)
        binding?.ayatRecycler?.layoutManager = LinearLayoutManager(requireContext())
        binding?.ayatRecycler?.adapter = adapter

        return binding?.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Default).launch {
            data.clear()
            BookmarkHelper(requireContext()).onReadAll().forEach {
                data.add(QuranData(requireContext()).getDataOfID(it))
            }
            activity?.runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun removed(pos: Int) {
//        data.removeAt(pos)
////        adapter.swap(data)
//        adapter.notifyItemRemoved(pos)
//        adapter.notifyItemRangeChanged(pos, data.size-pos)
    }
}