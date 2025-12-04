package com.example.smartexpensetrackerapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.smartexpensetrackerapp.databinding.BottomSheetAddAccountBinding
import com.example.smartexpensetrackerapp.data.ExpenseDatabase
import com.example.smartexpensetrackerapp.data.WalletAccount
import com.example.smartexpensetrackerapp.data.WalletRepository
import com.example.smartexpensetrackerapp.viewmodel.WalletViewModel
import com.example.smartexpensetrackerapp.viewmodel.WalletViewModelFactory

class AddAccountBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var walletViewModel: WalletViewModel

    interface OnAccountAddedListener {
        fun onAccountAdded()
    }

    private var callback: OnAccountAddedListener? = null

    fun setOnAccountAddedListener(listener: OnAccountAddedListener) {
        callback = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddAccountBinding.inflate(inflater, container, false)

        val database = ExpenseDatabase.getDatabase(requireContext())
        val repo = WalletRepository(database.walletAccountDao())
        val factory = WalletViewModelFactory(repo)

        walletViewModel =
            ViewModelProvider(requireActivity(), factory)[WalletViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveAccount.setOnClickListener {
            val name = binding.inputAccountName.text.toString().trim()
            val balanceText = binding.inputAccountBalance.text.toString().trim()
            val currencyInput = binding.inputAccountCurrency.text.toString().trim()

            if (name.isBlank() || balanceText.isBlank()) return@setOnClickListener

            val balance = balanceText.toDoubleOrNull() ?: return@setOnClickListener
            val currency = if (currencyInput.isBlank()) "BGN" else currencyInput.uppercase()

            val account = WalletAccount(
                name = name,
                balance = balance,
                currency = currency
            )

            walletViewModel.addAccount(account)
            callback?.onAccountAdded()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
