package com.code.apppraytime.http.interfaces

import com.code.apppraytime.model.HomeModel

interface LoadCallbackHome {
    fun onEmpty()
    fun onError(e: String)
    fun onLoaded(data: ArrayList<HomeModel?>)
}