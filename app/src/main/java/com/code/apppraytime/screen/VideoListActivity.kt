package com.code.apppraytime.screen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.code.apppraytime.adapter.VideoListAdapter
import com.code.apppraytime.constant.App
import com.code.apppraytime.databinding.ActivityVideoListBinding
import com.code.apppraytime.interfaces.HomeInterface
import com.code.apppraytime.model.VideoChildModel
import com.code.apppraytime.theme.ApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoListActivity : AppCompatActivity(), HomeInterface {

    companion object {
        fun launch(context: Context, title: String, id: String) {
            context.startActivity(
                Intent(
                    context,
                    VideoListActivity::class.java
                ).putExtra("ID", id)
                    .putExtra("TITLE", title)
            )
        }
    }

    var processing = true

    private val data = ArrayList<VideoChildModel?>()

    private val videoListAdapter by lazy {
        VideoListAdapter(
            this, data,
            intent.getStringExtra("TITLE")?:"",
            intent.getStringExtra("ID")?:"", this
        )
    }

    private val binding by lazy {
        ActivityVideoListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.title = intent.getStringExtra("TITLE")?:""
//        binding.back.setOnClickListener { finish() }
//        binding.title.text = intent.getStringExtra("TITLE")?:""

        binding.videoRecycler.run {
            layoutManager = LinearLayoutManager(this@VideoListActivity)
            adapter = videoListAdapter
            if (data.size==0) {
                data.add(null)
                loadPlaylists()
            }
        }
    }

    private fun loadPlaylists() {
        CoroutineScope(Dispatchers.Default).launch {
            (if (data.size<2) FirebaseDatabase.getInstance()
                .getReference("video")
                .child("data").child(intent.getStringExtra("ID")?:"")
                .orderByKey().limitToFirst(App.LOAD_ITEM_PER_QUERY)
            else FirebaseDatabase.getInstance().getReference("video")
                .child("data").child(intent.getStringExtra("ID")?:"")
                .orderByKey().startAfter(
                    data[data.size-2]?.position.toString()
                ).limitToFirst(App.LOAD_ITEM_PER_QUERY))
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var hasData = false
                            val size = data.size
                            data.removeAt(size-1)
                            runOnUiThread {
                                videoListAdapter.notifyItemRemoved(size - 1)
                            }
                            snapshot.children.forEach { snap->
                                data.add(
                                    VideoChildModel(
                                        snap.key!!.toInt(),
                                        snap.child("title").value.toString(),
                                        snap.child("url").value.toString()
                                    )
                                )
                                hasData = true
                            }
                            if (hasData) data.add(null)
                            runOnUiThread {
                                videoListAdapter.notifyItemRangeInserted(
                                    size, data.size - size
                                )
                                processing = false //!hasData
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@VideoListActivity,
                                error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
        }
    }

    override fun onBottom(id: String) {
        if (!processing) {
            processing = true
            loadPlaylists()
        }
    }
}