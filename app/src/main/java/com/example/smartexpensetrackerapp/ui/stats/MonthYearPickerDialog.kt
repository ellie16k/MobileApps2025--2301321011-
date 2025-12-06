package com.example.smartexpensetrackerapp.ui.stats

import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class MonthYearPickerDialog : DialogFragment() {

    private var listener: ((String, String) -> Unit)? = null

    fun setListener(callback: (String, String) -> Unit) {
        listener = callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(com.example.smartexpensetrackerapp.R.layout.dialog_month_year_picker, null)

        val monthPicker = view.findViewById<NumberPicker>(com.example.smartexpensetrackerapp.R.id.monthPicker)
        val yearPicker = view.findViewById<NumberPicker>(com.example.smartexpensetrackerapp.R.id.yearPicker)

        val months = arrayOf(
            "01","02","03","04","05","06","07","08","09","10","11","12"
        )

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.displayedValues = months

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = 2000
        yearPicker.maxValue = currentYear
        yearPicker.value = currentYear

        builder.setView(view)
            .setTitle("Select Month / Year")
            .setPositiveButton("OK") { _, _ ->
                val month = months[monthPicker.value - 1]
                val year = yearPicker.value.toString()
                listener?.invoke(month, year)
            }
            .setNegativeButton("Cancel", null)

        return builder.create()
    }
}
