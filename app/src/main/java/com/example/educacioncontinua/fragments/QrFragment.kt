package com.example.educacioncontinua.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.navArgs
import com.example.educacioncontinua.MainActivity
import com.example.educacioncontinua.R
import com.example.educacioncontinua.databinding.FragmentQrBinding
import com.example.educacioncontinua.dialogs.ErrorDialog
import com.example.educacioncontinua.dialogs.SuccessDialog
import com.example.educacioncontinua.interfaces.IErrorDialog
import com.example.educacioncontinua.interfaces.ISuccessDialog
import com.example.educacioncontinua.interfaces.RetrofitApi
import com.example.educacioncontinua.models.AssistanceResponse
import com.example.educacioncontinua.models.Course
import com.example.educacioncontinua.models.WorkingDay
import com.example.educacioncontinua.toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class QrFragment : Fragment(), AdapterView.OnItemClickListener, ISuccessDialog, IErrorDialog {

    private var _binding: FragmentQrBinding? = null
    private val binding get() = _binding!!
    private lateinit var course: Course
    private lateinit var journeys: List<WorkingDay>
    private lateinit var mainActivity: MainActivity
    private val args: QrFragmentArgs by navArgs()
    private var lastText: String? = null
    private var isModal = false
    private var idJourney = 0
    private val listJourneysString = mutableListOf<String>()
    private lateinit var dialog: Dialog

    @Inject
    lateinit var retrofitApi: RetrofitApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        course = args.course
        journeys = args.journeys.toList()
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        initView()
        if (checkCameraPermission()) {
            binding.textViewSinPermiso.visibility = View.GONE
            binding.barcodeScanner.visibility = View.VISIBLE
            initScanner()
        } else {
            binding.textViewSinPermiso.visibility = View.VISIBLE
            binding.barcodeScanner.visibility = View.GONE
        }
        fillAdapter()
        setUpDialogCheck()
        return binding.root
    }

    private fun initScanner() {
        with(binding) {
            val beepManager = BeepManager(mainActivity)
            barcodeScanner.setStatusText("")
            val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
            barcodeScanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
            barcodeScanner.initializeFromIntent(mainActivity.intent)
            barcodeScanner.decodeContinuous {
                if (it.text != null) {
                    beepManager.playBeepSoundAndVibrate()
                    if (lastText != null) {
                        if (lastText.equals(it.text)) {
                            pause()
                            openErrorDialog("El Qr ya fue leído con éxito")
                        }
                    } else {
                        dialog.show()
                        pause()
                        checkAttendance(it.text);
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    private fun initView() {
        binding.textViewTitulo.text = course.name
    }

    private fun checkCameraPermission(): Boolean =
        ActivityCompat.checkSelfPermission(
            mainActivity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun pause() {
        binding.barcodeScanner.pause();
    }

    private fun resume() {
        binding.barcodeScanner.resume();
    }

    private fun checkAttendance(resultQr: String) {
        val call = retrofitApi.assistance(course.id, idJourney, resultQr)
        call.enqueue(object : Callback<AssistanceResponse> {
            override fun onResponse(
                call: Call<AssistanceResponse>,
                response: Response<AssistanceResponse>
            ) {
                dialog.dismiss();
                try {
                    if (response.isSuccessful) {
                        lastText = resultQr
                        openSuccessDialog(response.body()!!)
                        toast("Asistencia Registrada")
                    } else {
                        lastText = null
                        openErrorDialog(msgError(response.code()))
                    }
                } catch (ex: Exception) {
                    lastText = null
                    toast("Error tipografico")
                }
            }

            override fun onFailure(call: Call<AssistanceResponse>, t: Throwable) {
                lastText = null
                resume()
                dialog.dismiss()
                toast("La peticion fallo.. vuelva a intentarlo")
            }

        })
    }


    private fun msgError(code: Int): String = when (code) {
        400 -> "No se encontro la jornada."
        409 -> "La asistecia ya fue registrada."
        412 -> "El participante no se encuentra inscrito."
        500 -> "El Qr es invalido."
        else -> "Error no identificado"
    }

    override fun onResume() {
        super.onResume()
        if (!isModal) {
            resume();
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isModal) {
            pause();
        }
    }


    private fun fillAdapter() {
        listJourneysString.clear()
        journeys.forEach { listJourneysString.add("${it.dayDateString} - ${it.startTimeString}") }
        fillDropdownMenu();
    }

    private fun fillDropdownMenu() {
        val adapter = ArrayAdapter(
            mainActivity,
            R.layout.dropdown_menu_popup_item,
            listJourneysString
        )
        binding.filledExposedDropdown.setAdapter(adapter);
        binding.filledExposedDropdown.setText(listJourneysString[0], false)
        getIds(listJourneysString[0]);
        binding.filledExposedDropdown.onItemClickListener = this
    }


    private fun getIds(selection: String) {
        journeys.forEach {
            val value = "${it.dayDateString} -  ${it.startTimeString}"
            if (value == selection) {
                idJourney = it.id
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            getIds(parent.getItemAtPosition(position).toString())
        }
    }

    private fun setUpDialogCheck(): Dialog {
        dialog = Dialog(mainActivity)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.progress_bar_jornadas)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onclick(dialog: SuccessDialog) {
        dialog.dismiss()
        isModal = false
        resume()
    }

    override fun onclick(dialog: ErrorDialog) {
        dialog.dismiss()
        isModal = false
        resume()
    }

    private fun openSuccessDialog(assistanceResponse: AssistanceResponse) {
        isModal = true
        val dialog = SuccessDialog.newInstance(assistanceResponse)
        dialog.show(parentFragmentManager, "successDialog")

    }

    private fun openErrorDialog(msg: String) {
        isModal = true
        val dialog = ErrorDialog.newInstance(msg)
        dialog.show(parentFragmentManager, "errorDialog")
    }

}