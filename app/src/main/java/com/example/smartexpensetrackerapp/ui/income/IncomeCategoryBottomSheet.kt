package com.example.smartexpensetrackerapp.ui.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.smartexpensetrackerapp.databinding.BottomSheetCategoriesBinding
import com.example.smartexpensetrackerapp.ui.categories.CategoryListAdapter

class IncomeCategoryBottomSheet(
    private val onCategorySelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCategoriesBinding? = null
    private val binding get() = _binding!!

    private val categories = listOf(
        "Salary",
        "Bonus",
        "Freelance",
        "Gift",
        "Interest",
        "Refund",
        "Other income"
    )

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

        val adapter = CategoryListAdapter(categories as MutableList<String>) { selected ->
            onCategorySelected(selected)
            dismiss()
        }

        binding.categoriesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.categoriesRecycler.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
