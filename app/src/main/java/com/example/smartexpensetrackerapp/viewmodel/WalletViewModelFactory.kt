package com.example.smartexpensetrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpensetrackerapp.data.WalletRepository

class WalletViewModelFactory(private val repo: WalletRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            return WalletViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
