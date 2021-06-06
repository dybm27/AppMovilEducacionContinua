package com.example.educacioncontinua.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.educacioncontinua.databinding.ModalJornadasExitoBinding
import com.example.educacioncontinua.interfaces.ISuccessDialog
import com.example.educacioncontinua.models.AssistanceResponse

class SuccessDialog : DialogFragment() {

    private var _binding: ModalJornadasExitoBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: ISuccessDialog

    companion object {
        fun newInstance(assistanceResponse: AssistanceResponse): SuccessDialog {
            val dialog = SuccessDialog()
            val arg = Bundle()
            arg.putParcelable("assistance", assistanceResponse)
            dialog.arguments = arg
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = ModalJornadasExitoBinding.inflate(inflater, container, false)
        savedInstanceState?.getParcelable<AssistanceResponse>("assistance")?.let { initView(it) }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    private fun initView(assistanceResponse: AssistanceResponse) {
        with(binding) {
            val text1 = SpannableString(assistanceResponse.name)
            text1.setSpan(UnderlineSpan(), 0, text1.length, 0)
            textViewNombreModal.text = text1
            val text2 = SpannableString(assistanceResponse.participantType)
            text2.setSpan(UnderlineSpan(), 0, text2.length, 0)
            textViewTipoModal.text = text2
            val text3 = SpannableString(assistanceResponse.document)
            text3.setSpan(UnderlineSpan(), 0, text3.length, 0)
            textViewDocumentoModal.text = text3
            btnModalExito.setOnClickListener { listener.onclick(this@SuccessDialog) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ISuccessDialog
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement mainFragmentCallback")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}