package com.example.smartexpensetrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensetrackerapp.data.WalletAccount
import com.example.smartexpensetrackerapp.data.WalletRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WalletViewModel(private val repo: WalletRepository) : ViewModel() {

    fun addAccount(account: WalletAccount) {
        viewModelScope.launch { repo.addAccount(account) }
    }

    fun updateAccount(account: WalletAccount) {
        viewModelScope.launch { repo.updateAccount(account) }
    }

    fun deleteAccount(account: WalletAccount) {
        viewModelScope.launch { repo.deleteAccount(account) }
    }

    val accounts = repo.getAllAccounts().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun getTotalBalance(): Double = repo.getTotalBalance()

    // Add money to a specific wallet
    fun addMoney(accountId: Int, amount: Double) {
        viewModelScope.launch {
            repo.addMoney(accountId, amount)
        }
    }
}
