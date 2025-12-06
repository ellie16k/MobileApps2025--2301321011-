package com.example.smartexpensetrackerapp.ui.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartexpensetrackerapp.data.*
import com.example.smartexpensetrackerapp.databinding.FragmentEditExpenseBinding
import com.example.smartexpensetrackerapp.ui.categories.CategoryBottomSheet
import com.example.smartexpensetrackerapp.ui.categories.CategoryType
import com.example.smartexpensetrackerapp.ui.home.WalletAccountSelectBottomSheet
import com.example.smartexpensetrackerapp.viewmodel.*
import kotlinx.coroutines.launch
import java.util.Calendar

class EditExpenseFragment : Fragment() {

    private var _binding: FragmentEditExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var walletViewModel: WalletViewModel

    private var expenseId: Int = -1
    private var originalExpense: Expense? = null

    private var selectedAccountId: Int? = null
    private var selectedAccountCurrency: String = "BGN"
    private var isIncome: Boolean = false   // we keep type fixed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        expenseId = arguments?.getInt("expenseId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditExpenseBinding.inflate(inflater, container, false)

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

        // 1) Load original expense and prefill UI
        viewLifecycleOwner.lifecycleScope.launch {
            val expense = expenseViewModel.getAllExpenses().find { it.id == expenseId }
            originalExpense = expense

            if (expense != null) {
                isIncome = expense.isIncome

                binding.titleEdit.text = if (isIncome) "EDIT INCOME" else "EDIT EXPENSE"

                binding.inputEditTitle.setText(expense.title)
                binding.inputEditCategory.setText(expense.category)
                binding.inputEditAmount.setText(expense.amount.toString())
                binding.inputEditDate.setText(expense.date)

                selectedAccountId = expense.accountId
                selectedAccountCurrency = expense.currency ?: "BGN"

                val accName = walletViewModel.accounts.value
                    .firstOrNull { it.id == expense.accountId }?.name
                if (accName != null) {
                    binding.inputEditAccount.setText(accName)
                }
            }
        }

        // CATEGORY SELECTOR (depends on income/expense)
        binding.inputEditCategory.setOnClickListener {
            val type = if (isIncome) CategoryType.INCOME else CategoryType.EXPENSE
            val sheet = CategoryBottomSheet(type) { category ->
                binding.inputEditCategory.setText(category)
            }
            sheet.show(parentFragmentManager, "editCategorySheet")
        }

        // ACCOUNT SELECTOR
        binding.inputEditAccount.setOnClickListener {
            val accounts = walletViewModel.accounts.value
            if (accounts.isEmpty()) {
                binding.inputEditAccount.error = "No accounts available"
                return@setOnClickListener
            }

            val sheet = WalletAccountSelectBottomSheet(
                accounts = accounts,
                onSelect = { account ->
                    binding.inputEditAccount.setText(account.name)
                    selectedAccountId = account.id
                    selectedAccountCurrency = account.currency
                }
            )
            sheet.show(parentFragmentManager, "editAccountSheet")
        }

        // DATE PICKER
        binding.inputEditDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                requireContext(),
                { _, y, m, d -> binding.inputEditDate.setText("$d/${m + 1}/$y") },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }

        // SAVE CHANGES
        binding.btnSaveEdit.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val original = originalExpense ?: return

        val newTitle = binding.inputEditTitle.text.toString()
        val newCategory = binding.inputEditCategory.text.toString()
        val newAmount = binding.inputEditAmount.text.toString().toDoubleOrNull()
        val newDate = binding.inputEditDate.text.toString()
        val newAccountId = selectedAccountId ?: original.accountId

        if (newTitle.isBlank() || newCategory.isBlank() || newAmount == null || newDate.isBlank() || newAccountId == null)
            return

        viewLifecycleOwner.lifecycleScope.launch {

            // 1) Adjust wallet balances
            val oldAmount = original.amount
            val oldAccountId = original.accountId
            val oldIsIncome = original.isIncome

            // If account changed
            if (oldAccountId != null && oldAccountId != newAccountId) {
                if (oldIsIncome) {
                    // undo old income
                    walletViewModel.subtractMoney(oldAccountId, oldAmount)
                    // apply new income
                    walletViewModel.addMoney(newAccountId, newAmount)
                } else {
                    // undo old expense
                    walletViewModel.addMoney(oldAccountId, oldAmount)
                    // apply new expense
                    walletViewModel.subtractMoney(newAccountId, newAmount)
                }
            } else if (oldAccountId != null) {
                // Same account, only amount changed
                val diff = newAmount - oldAmount
                if (diff != 0.0) {
                    if (oldIsIncome) {
                        if (diff > 0) {
                            walletViewModel.addMoney(oldAccountId, diff)
                        } else {
                            walletViewModel.subtractMoney(oldAccountId, -diff)
                        }
                    } else {
                        if (diff > 0) {
                            walletViewModel.subtractMoney(oldAccountId, diff)
                        } else {
                            walletViewModel.addMoney(oldAccountId, -diff)
                        }
                    }
                }
            }

            // 2) Update expense in DB
            val updated = original.copy(
                title = newTitle,
                category = newCategory,
                amount = newAmount,
                date = newDate,
                accountId = newAccountId,
                currency = selectedAccountCurrency
            )

            expenseViewModel.updateExpense(updated)

            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
