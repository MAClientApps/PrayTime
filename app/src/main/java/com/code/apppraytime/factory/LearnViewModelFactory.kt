package com.code.apppraytime.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.code.apppraytime.viewModel.LearnViewModel

class LearnViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearnViewModel::class.java))
            return LearnViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}