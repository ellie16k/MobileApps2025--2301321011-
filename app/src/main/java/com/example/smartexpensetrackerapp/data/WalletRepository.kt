package com.example.smartexpensetrackerapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WalletRepository(private val dao: WalletAccountDao) {

    suspend fun addAccount(account: WalletAccount) = dao.insertAccount(account)

    suspend fun updateAccount(account: WalletAccount) = dao.updateAccount(account)

    suspend fun deleteAccount(account: WalletAccount) = dao.deleteAccount(account)

    fun getAllAccounts(): Flow<List<WalletAccount>> = dao.getAllAccounts()

    suspend fun getTotalBalance(): Double {
        return dao.getAllAccounts().first().sumOf { it.balance }
    }

    // Add money to specific account
    suspend fun addMoney(accountId: Int, amount: Double) {
        val list = dao.getAllAccounts().first()
        val acc = list.firstOrNull { it.id == accountId } ?: return
        val updated = acc.copy(balance = acc.balance + amount)
        dao.updateAccount(updated)
    }
}
