package com.example.smartexpensetrackerapp.ui.add

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
import com.example.smartexpensetrackerapp.databinding.FragmentAddExpenseBinding
import com.example.smartexpensetrackerapp.ui.categories.CategoryBottomSheet
import com.example.smartexpensetrackerapp.ui.categories.CategoryType
import com.example.smartexpensetrackerapp.ui.home.WalletAccountSelectBottomSheet
import com.example.smartexpensetrackerapp.viewmodel.*
import kotlinx.coroutines.launch
import java.util.Calendar

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var walletViewModel: WalletViewModel

    private var selectedAccountId: Int? = null
    private var selectedAccountCurrency: String = "BGN"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)

        val db = ExpenseDatabase.getDatabase(requireContext())

        expenseViewModel = ViewModelProvider(
            requireActivity(),
            ExpenseViewModelFactory(ExpenseRepository(db.expenseDao()))
        )[ExpenseViewModel::class.java]

        walletViewModel = ViewModelProvider(
            requireActivity(),
            WalletViewModelFactory(WalletRepository(db.walletAccountDao()))
        )[WalletViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CATEGORY SELECTOR
        binding.inputCategory.setOnClickListener {
            val sheet = CategoryBottomSheet(CategoryType.EXPENSE) { category ->
                binding.inputCategory.setText(category)
            }
            sheet.show(parentFragmentManager, "expenseCategorySheet")
        }

        // WALLET ACCOUNT SELECTOR
        binding.inputWalletAccount.setOnClickListener {
            val accounts = walletViewModel.accounts.value

            if (accounts.isEmpty()) {
                binding.inputWalletAccount.error = "No accounts available"
                return@setOnClickListener
            }

            val sheet = WalletAccountSelectBottomSheet(
                accounts = accounts,
                onSelect = { account ->
                    binding.inputWalletAccount.setText(account.name)
                    selectedAccountId = account.id
                    selectedAccountCurrency = account.currency
                }
            )

            sheet.show(parentFragmentManager, "expenseAccountSheet")
        }

        // DATE PICKER
        binding.inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                requireContext(),
                { _, y, m, d -> binding.inputDate.setText("$d/${m + 1}/$y") },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }

        // SAVE BUTTON
        binding.btnSaveExpense.setOnClickListener {
            val category = binding.inputCategory.text.toString()
            val title = binding.inputTitle.text.toString()
            val amount = binding.inputAmount.text.toString().toDoubleOrNull()
            val date = binding.inputDate.text.toString()
            val accId = selectedAccountId

            if (category.isBlank() || title.isBlank() || amount == null || date.isBlank() || accId == null)
                return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {
                val expense = Expense(
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    isIncome = false,
                    accountId = accId,
                    currency = selectedAccountCurrency
                )

                expenseViewModel.addExpense(expense)
                walletViewModel.subtractMoney(accId, amount)

                findNavController().navigate(R.id.homeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
