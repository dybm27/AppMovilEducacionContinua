package com.ufps.geduco.ui.dialogs

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
import com.ufps.geduco.databinding.ModalJornadasErrorBinding
import com.ufps.geduco.ui.qr.QrFragment

class ErrorDialog : DialogFragment() {

    private var _binding: ModalJornadasErrorBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val EXTRA_SMG = "msg"

        fun newInstance(msg: String): ErrorDialog {
            return ErrorDialog().apply {
                arguments = bundleOf(EXTRA_SMG to msg)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = ModalJornadasErrorBinding.inflate(inflater, container, false)
        with(binding) {
            textViewErrorModal.text = arguments?.getString(EXTRA_SMG)
            btnModalError.setOnClickListener {
                dismiss()
                setFragmentResult(QrFragment.REQUEST_KEY, bundleOf())
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