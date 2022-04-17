package com.code.apppraytime.screen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.apppraytime.adapter.DuaRootAdapter
import com.code.apppraytime.constant.App
import com.code.apppraytime.constant.App.DUAS
import com.code.apppraytime.constant.App.INDEX
import com.code.apppraytime.databinding.ActivityDuaRootBinding
import com.code.apppraytime.interfaces.HomeInterface
import com.code.apppraytime.model.DuaRootModel
import com.code.apppraytime.theme.ApplicationTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DuaRootActivity : AppCompatActivity(), HomeInterface {

    companion object {
        fun launch(context: Context) {
            context.startActivity(
                Intent(
                    context,
                    DuaRootActivity::class.java
                )
            )
        }
    }

    private var processing = false
    private val data = ArrayList<DuaRootModel?>()
    private lateinit var adapter: DuaRootAdapter

    private lateinit var binding: ActivityDuaRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityDuaRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        if (data.size==0) data.add(null)

        adapter = DuaRootAdapter(this, data, this)
        binding.duaRecycler.layoutManager = LinearLayoutManager(this)
        binding.duaRecycler.adapter = adapter

        if (!processing) read(null)
    }

    private fun read(id: String?) {
        processing = true
        (if (id == null) FirebaseDatabase.getInstance().getReference(DUAS)
            .child(INDEX).orderByKey().limitToLast(App.LOAD_ITEM_PER_QUERY)
        else FirebaseDatabase.getInstance().getReference(DUAS)
            .child(INDEX).orderByKey().endBefore(id)
            .limitToLast(App.LOAD_ITEM_PER_QUERY))
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (data.size>0 && data[data.size-1]==null) {
                            data.removeAt(data.size-1)
                            adapter.notifyItemRemoved(data.size)
                        }
                        val size = data.size
                        snapshot.children.reversed().forEach {
                            data.add(it.getValue(DuaRootModel::class.java))
                        }
                        if (data.size > size) {
                            data.add(null)
                            adapter.notifyItemRangeInserted(size, data.size - size)
                        }
                        processing = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ERROR", error.toString())
                    }
                }
            )
    }

    override fun onBottom(id: String) {
        read(id)
        Log.e("ID", id)
    }
}