package com.example.smartexpensetrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpensetrackerapp.data.ExpenseRepository

class StatisticsViewModelFactory(
    private val repo: ExpenseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatisticsViewModel(repo) as T
    }
}
