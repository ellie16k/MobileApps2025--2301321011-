package com.example.smartexpensetrackerapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetrackerapp.R
import com.example.smartexpensetrackerapp.data.*
import com.example.smartexpensetrackerapp.databinding.FragmentHomeBinding
import com.example.smartexpensetrackerapp.viewmodel.*
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
    }

    private fun observeWalletData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            walletViewModel.accounts.collect { list ->
                val b = _binding ?: return@collect
                walletAdapter.updateData(list)
                val total = list.sumOf { it.balance }
                b.textTotalBalance.text = "Total: $total BGN"
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
