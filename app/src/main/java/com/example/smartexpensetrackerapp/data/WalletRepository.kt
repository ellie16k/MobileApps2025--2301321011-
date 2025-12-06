package com.example.smartexpensetrackerapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WalletRepository(private val dao: WalletAccountDao) {

    suspend fun addAccount(account: WalletAccount) = dao.insertAccount(account)

    suspend fun updateAccount(account: WalletAccount) = dao.updateAccount(account)

    suspend fun deleteAccount(account: WalletAccount) = dao.deleteAccount(account)

    fun getAllAccounts(): Flow<List<WalletAccount>> = dao.getAllAccounts()

    suspend fun getAccountById(id: Int): WalletAccount? = dao.getAccountById(id)

    

    suspend fun getTotalBalance(): Double {
        return dao.getAllAccounts().first().sumOf { it.balance }
    }

    suspend fun addMoney(accountId: Int, amount: Double) {
        val account = dao.getAccountById(accountId) ?: return
        val updated = account.copy(balance = account.balance + amount)
        dao.updateAccount(updated)
    }

    suspend fun subtractMoney(accountId: Int, amount: Double) {
        val account = dao.getAccountById(accountId) ?: return
        val updated = account.copy(balance = account.balance - amount)
        dao.updateAccount(updated)
    }
}
