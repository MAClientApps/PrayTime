package com.code.apppraytime.http

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.code.apppraytime.constant.App.LOAD_ITEM_PER_QUERY
import com.code.apppraytime.constant.App.POSTS
import com.code.apppraytime.http.interfaces.LoadCallbackHome
import com.code.apppraytime.model.HomeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class HomeLoader(val context: Context, val loadCallback: LoadCallbackHome) {

    private var lastID: String? = null

    fun read(id: String?) {
        lastID = id
        CoroutineScope(Dispatchers.Default).launch {
            (if (id == null) FirebaseDatabase.getInstance()
                .getReference(POSTS).orderByKey().limitToLast(LOAD_ITEM_PER_QUERY)
            else FirebaseDatabase.getInstance().getReference(POSTS)
                .orderByKey().endBefore(id).limitToLast(LOAD_ITEM_PER_QUERY))
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            process(snapshot)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            loadCallback.onError(error.message)
                        }
                    }
                )
        }
    }

    private fun process(snapshot: DataSnapshot) {
        var small = 0L
        val data = ArrayList<HomeModel?>()
        snapshot.children.reversed().forEach {
            it.key!!.toLong().let { l->
                if (l<small || small==0L) small = l
            }
            if (small != 0L) lastID = small.toString()
            data.add(it.getValue(HomeModel::class.java)!!.apply {
                id = lastID!!.toLong()
            })
        }
        if (data.isEmpty()) loadCallback.onEmpty()
        else loadCallback.onLoaded(data)
    }

//    private fun timeStamp(id: Long): String {
//        val cTime = ((System.currentTimeMillis() - id)/1000).toInt()
//        return if (cTime<82800) {
//            if (cTime>60) {
//                val min = cTime / 60
//                if (min > 60) {
//                    val hour = min / 60
//                    "$hour hours ago"
//                } else "$min minutes ago"
//            } else "Now"
//        } else  {
//            DateFormat.format(
//                "dd MMM yyyy",
//                Date(id)
//            ).toString()
//        }
//    }
}