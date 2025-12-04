package com.example.smartexpensetrackerapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletAccountDao {

    @Insert
    suspend fun insertAccount(account: WalletAccount)

    @Update
    suspend fun updateAccount(account: WalletAccount)

    @Delete
    suspend fun deleteAccount(account: WalletAccount)

    @Query("SELECT * FROM wallet_accounts")
    fun getAllAccounts(): Flow<List<WalletAccount>>

    @Query("SELECT * FROM wallet_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Int): WalletAccount?

    @Query("UPDATE wallet_accounts SET balance = balance + :amount WHERE id = :accountId")
    suspend fun addMoney(accountId: Int, amount: Double)

}
