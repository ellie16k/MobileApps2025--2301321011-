package com.example.smartexpensetrackerapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetrackerapp.data.WalletAccount
import com.example.smartexpensetrackerapp.databinding.ItemWalletAccountBinding

class WalletAccountAdapter(
    private var accounts: List<WalletAccount>
) : RecyclerView.Adapter<WalletAccountAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(val binding: ItemWalletAccountBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemWalletAccountBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.binding.textAccountName.text = account.name
        holder.binding.textAccountBalance.text =
            "${account.balance} ${account.currency}"
    }

    override fun getItemCount(): Int = accounts.size

    fun updateData(newList: List<WalletAccount>) {
        accounts = newList
        notifyDataSetChanged()
    }
}
