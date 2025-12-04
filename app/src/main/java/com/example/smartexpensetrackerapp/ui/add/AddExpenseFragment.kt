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
import com.example.smartexpensetrackerapp.ui.home.WalletAccountSelectBottomSheet
import com.example.smartexpensetrackerapp.viewmodel.*

import kotlinx.coroutines.launch
import java.util.Calendar

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var walletViewModel: WalletViewModel

    private var selectedWalletAccount: WalletAccount? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)

        val database = ExpenseDatabase.getDatabase(requireContext())

        // Expense ViewModel
        val expenseRepo = ExpenseRepository(database.expenseDao())
        val expenseFactory = ExpenseViewModelFactory(expenseRepo)
        expenseViewModel = ViewModelProvider(requireActivity(), expenseFactory)[ExpenseViewModel::class.java]

        // Wallet ViewModel
        val walletRepo = WalletRepository(database.walletAccountDao())
        val walletFactory = WalletViewModelFactory(walletRepo)
        walletViewModel = ViewModelProvider(requireActivity(), walletFactory)[WalletViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CATEGORY SELECTOR
        binding.inputCategory.setOnClickListener {
            val sheet = CategoryBottomSheet { category ->
                binding.inputCategory.setText(category)
            }
            sheet.show(parentFragmentManager, "categorySheet")
        }

        // WALLET ACCOUNT SELECTOR
        binding.inputWalletAccount.setOnClickListener {
            lifecycleScope.launch {
                val accounts = walletViewModel.accounts.value

                if (accounts.isEmpty()) {
                    binding.inputWalletAccount.setText("No accounts")
                    return@launch
                }

                WalletAccountSelectBottomSheet(
                    accounts,
                    onSelect = { account ->
                        selectedWalletAccount = account
                        binding.inputWalletAccount.setText("${account.name} (${account.currency})")
                    }
                ).show(parentFragmentManager, "selectAccountSheet")
            }
        }

        // DATE PICKER
        binding.inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    binding.inputDate.setText("$d/${m + 1}/$y")
                },
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
            val amountText = binding.inputAmount.text.toString()
            val date = binding.inputDate.text.toString()

            if (selectedWalletAccount == null ||
                category.isBlank() || title.isBlank() ||
                amountText.isBlank() || date.isBlank()
            ) return@setOnClickListener

            val amount = amountText.toDoubleOrNull() ?: return@setOnClickListener

            val expense = Expense(
                title = title,
                category = category,
                amount = amount,
                date = date
            )
            expenseViewModel.addExpense(expense)

            val wallet = selectedWalletAccount!!
            val updated = wallet.copy(balance = wallet.balance - amount)
            walletViewModel.updateAccount(updated)

            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
