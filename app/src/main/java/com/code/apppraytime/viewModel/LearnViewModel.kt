package com.code.apppraytime.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.code.apppraytime.model.VideoRootModel

class LearnViewModel : ViewModel() {

    val data: MutableLiveData<ArrayList<VideoRootModel?>> by lazy {
        MutableLiveData<ArrayList<VideoRootModel?>>()
    }
}