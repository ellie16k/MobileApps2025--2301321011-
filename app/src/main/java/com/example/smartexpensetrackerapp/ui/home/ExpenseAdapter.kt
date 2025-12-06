package com.example.smartexpensetrackerapp.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetrackerapp.data.Expense
import com.example.smartexpensetrackerapp.databinding.ItemExpenseBinding

class ExpenseAdapter(private var expenses: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    var onItemClick: ((Expense) -> Unit)? = null

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        val sign = if (expense.isIncome) "+" else "-"
        val formattedAmount = String.format("%.2f", expense.amount)
        val currency = expense.currency ?: ""

        holder.binding.textAmount.text = "$sign$formattedAmount $currency"

        val color = if (expense.isIncome) {
            Color.parseColor("#2E7D32")
        } else {
            Color.parseColor("#C62828")
        }
        holder.binding.textAmount.setTextColor(color)

        holder.binding.textCategory.text = expense.category
        holder.binding.textTitle.text = expense.title
        holder.binding.textDate.text = expense.date

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(expense)
        }
    }

    override fun getItemCount(): Int = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
