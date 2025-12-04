package com.example.smartexpensetrackerapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetrackerapp.databinding.BottomSheetSelectAccountBinding
import com.example.smartexpensetrackerapp.data.WalletAccount
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WalletAccountSelectBottomSheet(
    private val accounts: List<WalletAccount>,
    private val onSelect: (WalletAccount) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSelectAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSelectAccountBinding.inflate(inflater, container, false)

        val adapter = WalletAccountSelectAdapter(accounts) { selected ->
            onSelect(selected)
            dismiss()
        }

        binding.recyclerSelectAccount.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSelectAccount.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
