package com.example.educacioncontinua.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.educacioncontinua.databinding.ModalJornadasErrorBinding
import com.example.educacioncontinua.interfaces.IErrorDialog

class ErrorDialog : DialogFragment() {

    private lateinit var listener: IErrorDialog
    private var _binding: ModalJornadasErrorBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(msg: String): ErrorDialog {
            val dialog = ErrorDialog()
            val arg = Bundle()
            arg.putString("msg", msg)
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
        _binding = ModalJornadasErrorBinding.inflate(inflater, container, false)
        with(binding) {
            textViewErrorModal.text = savedInstanceState?.getString("msg")
            btnModalError.setOnClickListener { listener.onclick(this@ErrorDialog) }
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as IErrorDialog
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement mainFragmentCallback")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}