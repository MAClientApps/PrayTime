package com.code.apppraytime.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.code.apppraytime.viewModel.PlayerViewModel

class PlayerViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java))
            return PlayerViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}