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
import com.example.smartexpensetrackerapp.data.ExpenseDatabase
import com.example.smartexpensetrackerapp.data.ExpenseRepository
import com.example.smartexpensetrackerapp.databinding.FragmentHomeBinding
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModel
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Shared ViewModel across fragments
        val database = ExpenseDatabase.getDatabase(requireContext())
        val repository = ExpenseRepository(database.expenseDao())
        val factory = ExpenseViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory)[ExpenseViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Temporary: no UI logic here
    }

    override fun onResume() {
        super.onResume()
        // Temporary: no database loading
    }


    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup
        adapter = ExpenseAdapter(emptyList())
        binding.expensesRecyclerView.adapter = adapter
        binding.expensesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.addExpenseFragment)
        }
    }


    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val expenses = viewModel.getAllExpenses()
            adapter.updateData(expenses)

            binding.textEmpty.visibility =
                if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
