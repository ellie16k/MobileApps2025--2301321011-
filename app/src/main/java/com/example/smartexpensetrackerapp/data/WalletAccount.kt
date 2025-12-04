package com.example.smartexpensetrackerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_accounts")
data class WalletAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val balance: Double,
    val currency: String = "BGN"
)
