package com.example.smartexpensetrackerapp.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetrackerapp.databinding.BottomSheetCategoriesBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryBottomSheet(
    private val type: CategoryType,
    private val onCategorySelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCategoriesBinding? = null
    private val binding get() = _binding!!

    private val expenseCategories = listOf(
        "Food", "Transport", "Shopping", "Groceries",
        "Health", "Beauty", "Education", "Car",
        "Bills", "Travel", "Gifts", "Other expense"
    )

    private val incomeCategories = listOf(
        "Salary", "Bonus", "Scholarship", "Gift", "Other Income"
    )

    private val categories: List<String>
        get() = if (type == CategoryType.EXPENSE) expenseCategories else incomeCategories

    private lateinit var adapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CategoryListAdapter(categories.toMutableList()) { selected ->
            onCategorySelected(selected)
            dismiss()
        }

        binding.categoriesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.categoriesRecycler.adapter = adapter

        binding.searchCategory.addTextChangedListener { text ->
            val filtered = categories.filter {
                it.contains(text.toString(), ignoreCase = true)
            }
            adapter.updateList(filtered)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
