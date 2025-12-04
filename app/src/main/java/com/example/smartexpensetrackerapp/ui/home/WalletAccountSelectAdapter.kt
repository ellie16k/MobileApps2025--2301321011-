package com.example.smartexpensetrackerapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetrackerapp.data.WalletAccount
import com.example.smartexpensetrackerapp.databinding.ItemSelectWalletAccountBinding

class WalletAccountSelectAdapter(
    private val accounts: List<WalletAccount>,
    private val onClick: (WalletAccount) -> Unit
) : RecyclerView.Adapter<WalletAccountSelectAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(val binding: ItemSelectWalletAccountBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemSelectWalletAccountBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]

        holder.binding.textAccountName.text = account.name
        holder.binding.textAccountBalance.text =
            "${account.balance} ${account.currency}"

        holder.itemView.setOnClickListener { onClick(account) }
    }

    override fun getItemCount(): Int = accounts.size
}
