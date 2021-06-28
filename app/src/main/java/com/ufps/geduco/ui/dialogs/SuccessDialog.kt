package com.ufps.geduco.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.ufps.geduco.databinding.ModalJornadasExitoBinding
import com.ufps.geduco.data.model.Assistance
import com.ufps.geduco.ui.qr.QrFragment

class SuccessDialog : DialogFragment() {

    private var _binding: ModalJornadasExitoBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val EXTRA_ASSISTANCE = "assistance"

        fun newInstance(assistance: Assistance): SuccessDialog =
            SuccessDialog().apply {
                arguments = bundleOf(EXTRA_ASSISTANCE to assistance)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = ModalJornadasExitoBinding.inflate(inflater, container, false)
        arguments?.getParcelable<Assistance>(EXTRA_ASSISTANCE)?.let { initView(it) }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    private fun initView(assistance: Assistance) {
        with(binding) {
            val text1 = SpannableString(assistance.name)
            text1.setSpan(UnderlineSpan(), 0, text1.length, 0)
            textViewNombreModal.text = text1
            val text2 = SpannableString(assistance.participantType)
            text2.setSpan(UnderlineSpan(), 0, text2.length, 0)
            textViewTipoModal.text = text2
            val text3 = SpannableString(assistance.document)
            text3.setSpan(UnderlineSpan(), 0, text3.length, 0)
            textViewDocumentoModal.text = text3
            btnModalExito.setOnClickListener {
                dismiss()
                setFragmentResult(QrFragment.REQUEST_KEY, bundleOf())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}