package com.example.smartexpensetrackerapp.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetrackerapp.databinding.ItemCategoryBinding

class CategoryListAdapter(
    private var categories: MutableList<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.txtCategoryName.text = category

        holder.binding.root.setOnClickListener {
            onClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size

    fun updateList(newList: List<String>) {
        categories.clear()
        categories.addAll(newList)
        notifyDataSetChanged()
    }
}
