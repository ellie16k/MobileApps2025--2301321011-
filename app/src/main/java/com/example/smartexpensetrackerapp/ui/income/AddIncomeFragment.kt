package com.example.smartexpensetrackerapp.ui.income

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartexpensetrackerapp.R
import com.example.smartexpensetrackerapp.data.*
import com.example.smartexpensetrackerapp.databinding.FragmentAddIncomeBinding
import com.example.smartexpensetrackerapp.ui.categories.CategoryBottomSheet
import com.example.smartexpensetrackerapp.ui.categories.CategoryType
import com.example.smartexpensetrackerapp.ui.home.WalletAccountSelectBottomSheet
import com.example.smartexpensetrackerapp.viewmodel.*
import kotlinx.coroutines.launch
import java.util.Calendar

class AddIncomeFragment : Fragment() {

    private var _binding: FragmentAddIncomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var walletViewModel: WalletViewModel
    private lateinit var expenseViewModel: ExpenseViewModel

    private var selectedAccountId: Int? = null
    private var selectedAccountCurrency: String = "BGN"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeBinding.inflate(inflater, container, false)

        val db = ExpenseDatabase.getDatabase(requireContext())

        walletViewModel = ViewModelProvider(
            requireActivity(),
            WalletViewModelFactory(WalletRepository(db.walletAccountDao()))
        )[WalletViewModel::class.java]

        expenseViewModel = ViewModelProvider(
            requireActivity(),
            ExpenseViewModelFactory(ExpenseRepository(db.expenseDao()))
        )[ExpenseViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CATEGORY SELECTOR — INCOME ONLY
        binding.inputIncomeCategory.setOnClickListener {
            val sheet = CategoryBottomSheet(CategoryType.INCOME) { category ->
                binding.inputIncomeCategory.setText(category)
            }
            sheet.show(parentFragmentManager, "incomeCategorySheet")
        }

        // ACCOUNT SELECTOR
        binding.inputIncomeAccount.setOnClickListener {
            val sheet = WalletAccountSelectBottomSheet(
                accounts = walletViewModel.accounts.value,
                onSelect = { account ->
                    binding.inputIncomeAccount.setText(account.name)
                    selectedAccountId = account.id
                    selectedAccountCurrency = account.currency
                }
            )
            sheet.show(parentFragmentManager, "incomeAccountSheet")
        }

        // DATE PICKER
        binding.inputIncomeDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                requireContext(),
                { _, y, m, d -> binding.inputIncomeDate.setText("$d/${m + 1}/$y") },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }

        // SAVE INCOME
        binding.btnSaveIncome.setOnClickListener {
            val title = binding.inputIncomeTitle.text.toString()
            val category = binding.inputIncomeCategory.text.toString()
            val amount = binding.inputIncomeAmount.text.toString().toDoubleOrNull()
            val date = binding.inputIncomeDate.text.toString()
            val accId = selectedAccountId

            if (title.isBlank() || category.isBlank() || amount == null || date.isBlank() || accId == null)
                return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {
                val income = Expense(
                    title = title,
                    category = category,
                    amount = amount,
                    date = date,
                    isIncome = true,
                    accountId = accId,
                    currency = selectedAccountCurrency
                )
                expenseViewModel.addExpense(income)
                walletViewModel.addMoney(accId, amount)

                findNavController().navigate(R.id.homeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
