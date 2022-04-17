package com.code.apppraytime.screen.layout

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.apppraytime.R
import com.code.apppraytime.adapter.SurahListAdapter
import com.code.apppraytime.databinding.FragmentSurahBinding
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.IndexHelper

class SurahFrag : Fragment() {

    private var adapter: SurahListAdapter? = null
    private var _binding: FragmentSurahBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentSurahBinding.inflate(inflater, container, false)

        if(Application(requireContext()).layoutStyle==0)
            binding.frameLay.setBackgroundColor(ResourcesCompat.getColor(
                resources, R.color.frame, null
            ))

        adapter = SurahListAdapter(requireContext(), IndexHelper(requireContext()).readSurahAll())
        binding.surahRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.surahRecycler.adapter = adapter

        return _binding?.root
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}