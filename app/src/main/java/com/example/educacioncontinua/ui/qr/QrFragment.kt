package com.example.educacioncontinua.ui.qr

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.educacioncontinua.MainActivity
import com.example.educacioncontinua.R
import com.example.educacioncontinua.databinding.FragmentQrBinding
import com.example.educacioncontinua.ui.dialogs.ErrorDialog
import com.example.educacioncontinua.ui.dialogs.SuccessDialog
import com.example.educacioncontinua.data.RetrofitApi
import com.example.educacioncontinua.data.model.Assistance
import com.example.educacioncontinua.data.model.Course
import com.example.educacioncontinua.data.model.WorkingDay
import com.example.educacioncontinua.viewmodel.DataViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QrFragment : Fragment(), AdapterView.OnItemClickListener {

    private var _binding: FragmentQrBinding? = null
    private val binding get() = _binding!!
    private lateinit var course: Course
    private lateinit var workingDays: List<WorkingDay>
    private lateinit var mainActivity: MainActivity
    private val args: QrFragmentArgs by navArgs()
    private var lastText: String? = null
    private var isModal = false
    private var idJourney = 0
    private val listWorkingDaysString = mutableListOf<String>()
    private lateinit var dialog: Dialog

    private val model: DataViewModel by activityViewModels()

    @Inject
    lateinit var retrofitApi: RetrofitApi

    companion object {
        const val REQUEST_KEY = "qrFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        course = args.course
        workingDays = args.workingDays.toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog = mainActivity.setUpDialogCharging(getString(R.string.checking))
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        initView()
        initObserver()
        checkCameraPermission()
        fillAdapter()
        initFragmentResultListener()
        return binding.root
    }

    private fun initFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(REQUEST_KEY, this)
        { _, _ ->
            isModal = false
            resume()
        }
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
                    if (lastText != null && lastText.equals(it.text)) {
                        pause()
                        openErrorDialog("El Qr ya fue leído con éxito")
                    } else {
                        pause()
                        model.checkAttendance(course.id, idJourney, it.text)
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

    private fun checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.textViewSinPermiso.visibility = View.GONE
            binding.barcodeScanner.visibility = View.VISIBLE
            initScanner()
        } else {
            binding.textViewSinPermiso.visibility = View.VISIBLE
            binding.barcodeScanner.visibility = View.GONE
        }
    }

    private fun initObserver() {
        model.assistance.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { res ->
                openSuccessDialog(res)
            }
        })
        model.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { msg ->
                openErrorDialog(msg)
            }
        })
        model.isLoading.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { isLoading ->
                if (isLoading) {
                    dialog.show()
                } else {
                    dialog.dismiss()
                }
            }
        })
        model.valueQr.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { qr ->
                lastText = if (qr.isNotEmpty()) qr else null
            }
        })
    }


    private fun pause() = binding.barcodeScanner.pause()

    private fun resume() = binding.barcodeScanner.resume()

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
        listWorkingDaysString.clear()
        workingDays.forEach { listWorkingDaysString.add("${it.dayDateString} - ${it.startTimeString}") }
        fillDropdownMenu();
    }

    private fun fillDropdownMenu() {
        val adapter = ArrayAdapter(
            mainActivity,
            R.layout.dropdown_menu_popup_item,
            listWorkingDaysString
        )
        binding.filledExposedDropdown.setAdapter(adapter);
        binding.filledExposedDropdown.setText(listWorkingDaysString[0], false)
        getIds(listWorkingDaysString[0]);
        binding.filledExposedDropdown.onItemClickListener = this
    }


    private fun getIds(selection: String) {
        workingDays.forEach {
            val value = "${it.dayDateString} - ${it.startTimeString}"
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

    private fun openSuccessDialog(assistance: Assistance) {
        isModal = true
        SuccessDialog.newInstance(assistance).show(childFragmentManager, "successDialog")

    }

    private fun openErrorDialog(msg: String) {
        isModal = true
        ErrorDialog.newInstance(msg).show(childFragmentManager, "errorDialog")
    }

}