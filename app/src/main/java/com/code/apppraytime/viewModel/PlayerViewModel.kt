package com.code.apppraytime.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.code.apppraytime.model.VideoChildModel

class PlayerViewModel : ViewModel() {

    val videoList : MutableLiveData<ArrayList<VideoChildModel?>> by lazy {
        MutableLiveData<ArrayList<VideoChildModel?>>()
    }

    val videoPosition: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val milliSec: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val fullScreen: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val courseId: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}