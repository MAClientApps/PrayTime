package com.code.apppraytime.screen.layout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.apppraytime.R
import com.code.apppraytime.adapter.ParaListAdapter
import com.code.apppraytime.databinding.FragmentParaBinding
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.IndexHelper

class ParaFrag : Fragment() {

    private var _binding: FragmentParaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentParaBinding.inflate(inflater, container, false)

        if(Application(requireContext()).layoutStyle==0)
            binding.frameLay.setBackgroundColor(
                ResourcesCompat.getColor(
                resources, R.color.frame, null
            ))

        binding.paraRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.paraRecycler.adapter = ParaListAdapter(
            requireContext(), IndexHelper(requireContext()).readParaAll()
        )

        return _binding?.root
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}