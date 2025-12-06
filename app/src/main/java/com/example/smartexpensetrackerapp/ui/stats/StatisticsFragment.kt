package com.example.smartexpensetrackerapp.ui.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetrackerapp.data.CategoryTotal
import com.example.smartexpensetrackerapp.data.ExpenseDatabase
import com.example.smartexpensetrackerapp.data.ExpenseRepository
import com.example.smartexpensetrackerapp.databinding.FragmentStatisticsBinding
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModel
import com.example.smartexpensetrackerapp.viewmodel.ExpenseViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.Calendar
import kotlin.math.abs

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: ExpenseViewModel
    private lateinit var categoryAdapter: CategoryTotalsAdapter

    private var selectedMonth: String = ""
    private var selectedYear: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        val db = ExpenseDatabase.getDatabase(requireContext())
        val repo = ExpenseRepository(db.expenseDao())
        vm = ViewModelProvider(
            requireActivity(),
            ExpenseViewModelFactory(repo)
        )[ExpenseViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAdapter = CategoryTotalsAdapter(emptyList(), "")
        binding.recyclerCategoryTotals.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategoryTotals.adapter = categoryAdapter

        binding.monthSelector.setOnClickListener {
            showMonthYearPicker()
        }

        loadStatistics()
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            val now = Calendar.getInstance()

            val month = if (selectedMonth.isNotEmpty()) selectedMonth
            else String.format("%02d", now.get(Calendar.MONTH) + 1)

            val year = if (selectedYear.isNotEmpty()) selectedYear
            else now.get(Calendar.YEAR).toString()

            binding.textSelectedMonth.text = getMonthLabel(month, year)

            // Load all expenses and filter by month/year
            val allExpenses = vm.getAllExpenses()
            val monthList = allExpenses.filter { expense ->
                val parts = expense.date.split("/")
                if (parts.size >= 3) {
                    val expMonth = parts[1].padStart(2, '0')
                    val expYear = parts[2]
                    expMonth == month && expYear == year
                } else false
            }


            // INCOME & EXPENSE SUMMARY
            val incomeByCurrency = monthList
                .filter { it.isIncome }
                .groupBy { it.currency }
                .mapValues { (_, list) -> list.sumOf { it.amount } }

            val expenseByCurrency = monthList
                .filter { !it.isIncome }
                .groupBy { it.currency }
                .mapValues { (_, list) -> list.sumOf { it.amount } }

            val incomeText = if (incomeByCurrency.isEmpty()) {
                "Income: +0.00"
            } else {
                incomeByCurrency.entries.joinToString("\n") { (currency, total) ->
                    "Income ($currency): +%.2f".format(total)
                }
            }

            val expenseText = if (expenseByCurrency.isEmpty()) {
                "Expense: -0.00"
            } else {
                expenseByCurrency.entries.joinToString("\n") { (currency, total) ->
                    "Expense ($currency): -%.2f".format(total)
                }
            }

            binding.textIncome.text = incomeText
            binding.textExpense.text = expenseText

            // TOTALS BY CURRENCY
            val totalsByCurrency = monthList
                .groupBy { it.currency }
                .mapValues { (_, list) ->
                    list.sumOf { e ->
                        if (e.isIncome) e.amount else -e.amount
                    }
                }

            val totalsText = if (totalsByCurrency.isEmpty()) {
                "Total This Month: 0.00"
            } else {
                totalsByCurrency.entries.joinToString(
                    prefix = "Total This Month:\n",
                    separator = "\n"
                ) { (currency, total) ->
                    "$currency %.2f".format(total)
                }
            }

            binding.textTotalMonth.text = totalsText

            // CATEGORY TOTALS
            val categoryTotals = monthList
                .groupBy { it.category }
                .map { (category, list) ->
                    val catTotal = list.sumOf { e ->
                        if (e.isIncome) e.amount else -e.amount
                    }
                    CategoryTotal(category, abs(catTotal)) // IMPORTANT FIX
                }

            val primaryCurrency = monthList.firstOrNull()?.currency ?: ""
            categoryAdapter.updateData(categoryTotals, primaryCurrency)

            setupPieChart(categoryTotals)
        }
    }

    private fun setupPieChart(categories: List<CategoryTotal>) {
        val pieChart: PieChart = binding.pieChart

        if (categories.isEmpty()) {
            pieChart.clear()
            pieChart.centerText = "No Data"
            return
        }

        val entries = ArrayList<PieEntry>()

        // Add dummy slice when there is only one category
        if (categories.size == 1) {
            val only = categories[0]
            entries.add(PieEntry(abs(only.total.toFloat()), only.category))
            entries.add(PieEntry(0.0001f, "")) // invisible filler
        } else {
            entries.addAll(categories.map {
                PieEntry(abs(it.total.toFloat()), it.category)
            })
        }

        val colors = mutableListOf(
            Color.parseColor("#7E57C2"),
            Color.parseColor("#5C6BC0"),
            Color.parseColor("#26A69A"),
            Color.parseColor("#FF7043"),
            Color.parseColor("#EC407A"),
            Color.parseColor("#42A5F5")
        )

        if (categories.size == 1) {
            colors.add(Color.TRANSPARENT) // dummy slice color
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextSize = 0f
        dataSet.setDrawValues(false)
        dataSet.sliceSpace = 2f

        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 55f
        pieChart.transparentCircleRadius = 60f
        pieChart.setDrawEntryLabels(false)
        pieChart.centerText = "Categories"
        pieChart.setCenterTextSize(16f)

        pieChart.rotationAngle = 270f

        val legend = pieChart.legend
        legend.textSize = 12f
        legend.formSize = 12f
        legend.xEntrySpace = 10f
        legend.yEntrySpace = 5f
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        pieChart.description.isEnabled = false
        pieChart.animateY(800)
        pieChart.invalidate()
    }

    private fun showMonthYearPicker() {
        val dialog = MonthYearPickerDialog()
        dialog.setListener { month, year ->
            selectedMonth = month
            selectedYear = year
            loadStatistics()
        }
        dialog.show(parentFragmentManager, "MonthYearPickerDialog")
    }

    private fun getMonthLabel(month: String, year: String): String {
        val index = month.toIntOrNull()?.minus(1) ?: return "$month/$year"
        val name = DateFormatSymbols().months.getOrNull(index) ?: month
        return "$name $year"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
