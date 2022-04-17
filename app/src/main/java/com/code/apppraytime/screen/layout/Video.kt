package com.code.apppraytime.screen.layout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.code.apppraytime.adapter.VideoRootAdapter
import com.code.apppraytime.constant.App
import com.code.apppraytime.databinding.FragmentVideoBinding
import com.code.apppraytime.factory.LearnViewModelFactory
import com.code.apppraytime.interfaces.LearnRoot
import com.code.apppraytime.model.Resource
import com.code.apppraytime.model.VideoChildModel
import com.code.apppraytime.model.VideoRootModel
import com.code.apppraytime.shared.Application
import com.code.apppraytime.viewModel.LearnViewModel
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Video : Fragment(), LearnRoot {

    private var processing = false

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(),
            LearnViewModelFactory()
        )[LearnViewModel::class.java]
    }

    private val adapter by lazy {
        VideoRootAdapter(requireContext(), this, viewModel.data.value)
    }

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        requireActivity().intent.getParcelableArrayListExtra<VideoRootModel>("VIDEO_DATA")
            ?.let {
                viewModel.data.value = it
            }

        if(viewModel.data.value == null) {
            viewModel.data.value = ArrayList()
            viewModel.data.value?.add(null)
            if(!processing) {
                processing  = true
                loadVideos()
            }
        }
        binding.recyclerViewVideo.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVideo.adapter = adapter

        return _binding?.root
    }

    private fun loadVideos() {
        CoroutineScope(Dispatchers.Default).launch {
            (if (viewModel.data.value!!.size<2)
                FirebaseDatabase.getInstance()
                .getReference("video")
                .child("index")
                .orderByKey().limitToLast(App.LOAD_ITEM_PER_QUERY)
            else
                FirebaseDatabase.getInstance().getReference("video")
                .child("index").orderByKey()
                .endBefore("${viewModel.data.value!![viewModel.data.value!!.size-2]?.id}")
                .limitToLast(App.LOAD_ITEM_PER_QUERY))
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var hasData = false
                            val size = viewModel.data.value!!.size
                            viewModel.data.value?.removeAt(size-1)
                            activity?.runOnUiThread {
                                adapter.notifyItemRemoved(size - 1)
                            }
                            snapshot.children.reversed().forEach { snap->
                                val temp = VideoRootModel(
                                    id = snap.key!!.toLong(),
                                    title = snap.child("0").value.toString(),
                                    data = ArrayList<VideoChildModel?>().also {
                                        snap.children.forEach { s->
                                            if (s.key.toString() != "0") {
                                                val re = decodeVideoUrl(s.key.toString())
                                                it.add(
                                                    VideoChildModel(
                                                        re.pos,
                                                        s.value.toString(),
                                                        re.url
                                                    )
                                                )
                                            }
                                        }
                                        it.add(null)
                                    }
                                )
                                viewModel.data.value?.add(temp)
                                hasData = true
                            }
                            if (hasData) viewModel.data.value?.add(null)
                            activity?.runOnUiThread {
                                adapter.notifyItemRangeInserted(
                                    size, viewModel.data.value!!.size - size
                                )
                                processing = false //!hasData
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                requireContext(),
                                error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
        }
    }

    private fun decodeVideoUrl(url: String): Resource {
        url.indexOf(") ").let {
            return Resource(url.substring(
                url.indexOf("(")+1, it).toInt()
                , url.substring(it+2)
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    override fun onBottom() {
        if (!processing) {
            processing = true
            loadVideos()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Application(requireContext()).firstLaunch) {
            val manager = ReviewManagerFactory.create(requireActivity())
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener {
                        Log.e("Complete", "Done")
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            Application(requireContext()).firstLaunch = false
        }
    }
}