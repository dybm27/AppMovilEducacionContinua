package com.example.educacioncontinua.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.educacioncontinua.databinding.LayoutLogoutDialogBinding
import com.example.educacioncontinua.ui.home.HomeFragment

class LogoutDialog : DialogFragment() {

    private var _binding: LayoutLogoutDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = LayoutLogoutDialogBinding.inflate(inflater, container, false)
        with(binding) {
            buttonLogoutDialogAccept.setOnClickListener {
                dismiss()
                setFragmentResult(HomeFragment.REQUEST_KEY, bundleOf())
            }
            buttonLogoutDialogCancel.setOnClickListener {
                dismiss()
            }
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}