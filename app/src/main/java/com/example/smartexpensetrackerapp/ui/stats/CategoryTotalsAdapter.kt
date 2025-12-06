package com.example.smartexpensetrackerapp.ui.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetrackerapp.R
import com.example.smartexpensetrackerapp.data.CategoryTotal

class CategoryTotalsAdapter(
    private var totals: List<CategoryTotal>,
    private var currency: String
) : RecyclerView.Adapter<CategoryTotalsAdapter.ViewHolder>() {

    private var maxAmount: Float = 1f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_total, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = totals[position]

        val amountText = if (currency.isNotBlank()) {
            "$currency %.2f".format(item.total)
        } else {
            "%.2f".format(item.total)
        }
        holder.categoryName.text = item.category
        holder.categoryTotal.text = amountText

        val totalValue = item.total.toFloat()

        val percent = if (maxAmount > 0f) {
            ((totalValue / maxAmount) * 100f).toInt()
        } else {
            0
        }

        holder.progress.progress = percent
    }

    override fun getItemCount(): Int = totals.size

    fun updateData(newTotals: List<CategoryTotal>, newCurrency: String) {
        totals = newTotals
        currency = newCurrency
        maxAmount = newTotals.maxOfOrNull { it.total.toFloat() } ?: 1f
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.textCategoryName)
        val categoryTotal: TextView = view.findViewById(R.id.textCategoryTotal)
        val progress: ProgressBar = view.findViewById(R.id.progressCategory)
    }
}
