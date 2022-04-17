package com.code.apppraytime.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.code.apppraytime.model.*

class HomeViewModel : ViewModel() {

    val noAnimation: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val locationLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val postId: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val location : MutableLiveData<LocModel> by lazy {
        MutableLiveData<LocModel>()
    }

    val locationName : MutableLiveData<LocModelName> by lazy {
        MutableLiveData<LocModelName>()
    }

    val salatTime : MutableLiveData<SalatModel> by lazy {
        MutableLiveData<SalatModel>()
    }

    val salatTimeList : MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>()
    }

    val date : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val homeData : MutableLiveData<ArrayList<HomeModel?>> by lazy {
        MutableLiveData<ArrayList<HomeModel?>>()
    }

    val sunData : MutableLiveData<Sun> by lazy {
        MutableLiveData<Sun>()
    }

    val refresh : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun addValue(data: ArrayList<HomeModel?>) {
        homeData.value?.run {
            val temp = homeData.value
            temp?.addAll(data)
            homeData.value = temp
        }?: run {
            homeData.value = data
        }
        Log.e("DATA", homeData.value!!.size.toString())
    }

    fun removeValue(at: Int) {
        homeData.value?.run {
            val temp = homeData.value
            temp?.removeAt(at)
            homeData.value = temp
        }
    }
}