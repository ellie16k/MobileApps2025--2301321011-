package com.example.smartexpensetrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensetrackerapp.data.CategoryTotal
import com.example.smartexpensetrackerapp.data.Expense
import com.example.smartexpensetrackerapp.data.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insert(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.update(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    suspend fun getAllExpenses(): List<Expense> {
        return repository.getAllExpenses()
    }

    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insert(expense)
        }
    }

    suspend fun getExpensesForMonth(month: String, year: String): List<Expense> {
        return repository.getExpensesForMonth(month, year)
    }

    suspend fun getTotalsByCategory(): List<CategoryTotal> {
        return repository.getTotalsByCategory()
    }

}
