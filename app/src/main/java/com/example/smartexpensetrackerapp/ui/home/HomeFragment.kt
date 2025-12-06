package com.example.smartexpensetrackerapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetrackerapp.R
import com.example.smartexpensetrackerapp.data.ExpenseDatabase
import com.example.smartexpensetrackerapp.data.ExpenseRepository
import com.example.smartexpensetrackerapp.data.WalletRepository
import com.example.smartexpensetrackerapp.databinding.FragmentHomeBinding
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModel
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModelFactory
import com.example.smartexpensetrackerapp.viewmodel.WalletViewModel
import com.example.smartexpensetrackerapp.viewmodel.WalletViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var walletViewModel: WalletViewModel
    private lateinit var walletAdapter: WalletAccountAdapter

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var recentAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

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

        setupWalletList()
        setupRecentList()
        observeWalletData()
        loadRecentTransactions()

        binding.buttonAddAccount.setOnClickListener {
            val sheet = AddAccountBottomSheet()
            sheet.setOnAccountAddedListener(object : AddAccountBottomSheet.OnAccountAddedListener {
                override fun onAccountAdded() {
                    // Flow will update UI automatically
                }
            })
            sheet.show(parentFragmentManager, "addAccountSheet")
        }

        binding.textSeeMore.setOnClickListener {
            findNavController().navigate(R.id.transactionsFragment)
        }
    }

    private fun setupWalletList() {
        walletAdapter = WalletAccountAdapter(emptyList())
        binding.recyclerAccounts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAccounts.adapter = walletAdapter
    }

    private fun setupRecentList() {
        recentAdapter = ExpenseAdapter(emptyList())
        binding.recyclerRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecent.adapter = recentAdapter

        recentAdapter.onItemClick = { expense ->
            val bundle = Bundle().apply {
                putInt("expenseId", expense.id)
            }
            findNavController().navigate(R.id.expenseDetailsFragment, bundle)
        }
    }

    private fun observeWalletData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                walletViewModel.accounts.collect { list ->
                    walletAdapter.updateData(list)

                    val grouped = list.groupBy { it.currency }
                    val sortedTotals = grouped.entries
                        .map { (currency, accounts) ->
                            currency to accounts.sumOf { it.balance }
                        }
                        .sortedByDescending { it.second }

                    val totalText = sortedTotals.joinToString("\n") { (currency, sum) ->
                        "• %.2f %s".format(sum, currency)
                    }

                    binding.textTotalBalance.text = "Total:\n$totalText"
                }
            }
        }
    }

    private fun loadRecentTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            val all = expenseViewModel.getAllExpenses()
            val lastThree = all.takeLast(3).reversed()
            recentAdapter.updateData(lastThree)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
