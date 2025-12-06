package com.example.smartexpensetrackerapp.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartexpensetrackerapp.data.ExpenseDatabase
import com.example.smartexpensetrackerapp.data.ExpenseRepository
import com.example.smartexpensetrackerapp.data.WalletRepository
import com.example.smartexpensetrackerapp.databinding.FragmentExpenseDetailsBinding
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModel
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModelFactory
import com.example.smartexpensetrackerapp.viewmodel.WalletViewModel
import com.example.smartexpensetrackerapp.viewmodel.WalletViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ExpenseDetailsFragment : Fragment() {

    private var _binding: FragmentExpenseDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var walletViewModel: WalletViewModel
    private var expenseId: Int = -1
    private var currentExpense: com.example.smartexpensetrackerapp.data.Expense? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseDetailsBinding.inflate(inflater, container, false)

        val db = ExpenseDatabase.getDatabase(requireContext())

        expenseViewModel = ViewModelProvider(
            requireActivity(),
            ExpenseViewModelFactory(ExpenseRepository(db.expenseDao()))
        )[ExpenseViewModel::class.java]

        walletViewModel = ViewModelProvider(
            requireActivity(),
            WalletViewModelFactory(WalletRepository(db.walletAccountDao()))
        )[WalletViewModel::class.java]

        // Read expenseId from arguments Bundle
        expenseId = arguments?.getInt("expenseId", -1) ?: -1

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (expenseId == -1) {
            findNavController().navigateUp()
            return
        }

        // Load and show details
        viewLifecycleOwner.lifecycleScope.launch {
            currentExpense = expenseViewModel.getAllExpenses().find { it.id == expenseId }

            currentExpense?.let { expense ->
                binding.detailsTitle.text = expense.title
                binding.detailsCategory.text = expense.category

                val sign = if (expense.isIncome) "+" else "-"
                val currency = expense.currency ?: ""
                binding.detailsAmount.text = "$sign${expense.amount} $currency"

                binding.detailsDate.text = expense.date
            }
        }


        binding.btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("expenseId", expenseId)
            }
            findNavController().navigate(
                com.example.smartexpensetrackerapp.R.id.editExpenseFragment,
                bundle
            )
        }

        // DELETE button
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun deleteExpense() {
        val expense = currentExpense ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val accId = expense.accountId ?: return@launch

            if (expense.isIncome) {
                walletViewModel.subtractMoney(accId, expense.amount)
            } else {
                walletViewModel.addMoney(accId, expense.amount)
            }

            expenseViewModel.deleteExpense(expense)

            Snackbar.make(requireView(), "Expense deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    // Undo wallet adjustment
                    if (expense.isIncome) {
                        walletViewModel.addMoney(accId, expense.amount)
                    } else {
                        walletViewModel.subtractMoney(accId, expense.amount)
                    }

                    expenseViewModel.insertExpense(expense)
                }
                .show()

            findNavController().popBackStack()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Delete") { _, _ ->
                deleteExpense()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
