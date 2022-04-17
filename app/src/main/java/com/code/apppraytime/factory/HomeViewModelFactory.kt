package com.code.apppraytime.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.code.apppraytime.viewModel.HomeViewModel

class HomeViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}