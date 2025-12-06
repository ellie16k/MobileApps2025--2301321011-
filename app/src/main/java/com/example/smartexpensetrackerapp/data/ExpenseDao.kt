package com.example.smartexpensetrackerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)


    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    suspend fun getExpensesForMonth(month: String, year: String): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category")
    suspend fun getTotalByCategory(category: String): Double?

    @Query("SELECT category, SUM(amount) AS total FROM expenses GROUP BY category")
    suspend fun getTotalsGroupedByCategory(): List<CategoryTotal>


}
