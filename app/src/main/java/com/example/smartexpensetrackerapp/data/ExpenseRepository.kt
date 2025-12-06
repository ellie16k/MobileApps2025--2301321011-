package com.example.smartexpensetrackerapp.data

class ExpenseRepository(private val dao: ExpenseDao) {

    suspend fun insert(expense: Expense) {
        dao.insertExpense(expense)
    }

    suspend fun update(expense: Expense) {
        dao.updateExpense(expense)
    }

    suspend fun delete(expense: Expense) {
        dao.deleteExpense(expense)
    }

    suspend fun getAllExpenses(): List<Expense> {
        return dao.getAllExpenses()
    }

    suspend fun getExpensesForMonth(month: String, year: String): List<Expense> {
        return dao.getExpensesForMonth(month, year)
    }

    suspend fun getTotalsByCategory(): List<CategoryTotal> {
        return dao.getTotalsGroupedByCategory()
    }

}
