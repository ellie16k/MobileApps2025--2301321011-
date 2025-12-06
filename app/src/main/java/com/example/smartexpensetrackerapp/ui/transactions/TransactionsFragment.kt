package com.example.smartexpensetrackerapp.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetrackerapp.R
import com.example.smartexpensetrackerapp.data.ExpenseDatabase
import com.example.smartexpensetrackerapp.data.ExpenseRepository
import com.example.smartexpensetrackerapp.databinding.FragmentTransactionsBinding
import com.example.smartexpensetrackerapp.ui.home.ExpenseAdapter
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModel
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModelFactory
import kotlinx.coroutines.launch

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseAdapter

    private var fullList: List<com.example.smartexpensetrackerapp.data.Expense> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        val db = ExpenseDatabase.getDatabase(requireContext())
        val repo = ExpenseRepository(db.expenseDao())
        viewModel = ViewModelProvider(
            requireActivity(),
            ExpenseViewModelFactory(repo)
        )[ExpenseViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Recycler
        adapter = ExpenseAdapter(emptyList())
        binding.recyclerAllTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAllTransactions.adapter = adapter

        adapter.onItemClick = { expense ->
            val bundle = Bundle().apply {
                putInt("expenseId", expense.id)
            }
            findNavController().navigate(R.id.expenseDetailsFragment, bundle)
        }

        // Load Data
        viewLifecycleOwner.lifecycleScope.launch {
            fullList = viewModel.getAllExpenses().reversed()
            adapter.updateData(fullList)
        }

        // BACK arrow action
        binding.transactionsToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // MENU actions
        binding.transactionsToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_sort -> {
                    showSortMenu()
                    true
                }

                R.id.action_filter -> {
                    showFilterMenu()
                    true
                }

                else -> false
            }
        }
    }

    private fun showSortMenu() {
        val popup = PopupMenu(requireContext(), binding.transactionsToolbar)
        popup.menu.apply {
            add("Newest first")
            add("Oldest first")
            add("Amount high → low")
            add("Amount low → high")
        }

        popup.setOnMenuItemClickListener { item ->
            val sorted = when (item.title) {
                "Newest first" -> fullList.reversed()
                "Oldest first" -> fullList
                "Amount high → low" -> fullList.sortedByDescending { it.amount }
                "Amount low → high" -> fullList.sortedBy { it.amount }
                else -> fullList
            }
            adapter.updateData(sorted)
            true
        }

        popup.show()
    }

    private fun showFilterMenu() {
        val popup = PopupMenu(requireContext(), binding.transactionsToolbar)
        popup.menu.apply {
            add("All")
            add("Expenses only")
            add("Income only")
        }

        popup.setOnMenuItemClickListener { item ->
            val filtered = when (item.title) {
                "Expenses only" -> fullList.filter { !it.isIncome }
                "Income only"   -> fullList.filter { it.isIncome }
                else            -> fullList
            }

            adapter.updateData(filtered)
            true
        }

        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
