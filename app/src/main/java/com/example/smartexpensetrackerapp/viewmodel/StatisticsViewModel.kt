package com.example.smartexpensetrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.smartexpensetrackerapp.data.ExpenseRepository

class StatisticsViewModel(private val repo: ExpenseRepository) : ViewModel() {

    suspend fun getExpensesForMonth(month: String, year: String) =
        repo.getExpensesForMonth(month, year)

    suspend fun getTotalsByCategory() =
        repo.getTotalsByCategory()
}
