package com.example.educacioncontinua.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.educacioncontinua.R;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.models.Jornada;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JornadasQrFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private List<Jornada> jornadas;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayListJornadas = new ArrayList<>();
    private DecoratedBarcodeView barcodeView;
    private boolean isFlashOn;
    private static final String TAG = "ScanQRActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jornadas_qr, container, false);
        Bundle datosRecuperados = getArguments();
        isFlashOn = false;
        if (datosRecuperados == null) {
            ToastrConfig.mensaje(getContext(), "No se pudieron cargar los datos");
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }
        assert datosRecuperados != null;
        jornadas = datosRecuperados.getParcelableArrayList("jornadas");
        upScanner1(view);
        llenarAdapter(view);
        return view;
    }

    public void llenarAdapter(View view) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        for (int i = 0; i < jornadas.size(); i++) {
            String strDate = dateFormat.format(jornadas.get(i).getHoraFin());
            arrayListJornadas.add(strDate);
        }
        System.out.println("----------------------------------------------------------");
        for (int i = 0; i < arrayListJornadas.size(); i++) {
            System.out.println(arrayListJornadas.get(i));
        }
        llenarMenuDesplegable(view);

    }

    public void llenarMenuDesplegable(View view) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        arrayListJornadas);

        AutoCompleteTextView editTextFilledExposedDropdown =
                view.findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setText(arrayListJornadas.get(0), false);
        editTextFilledExposedDropdown.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void upScanner1(View view) {
        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.barcode_view);

        CameraSettings ca = new CameraSettings();
        ca.setRequestedCameraId(0);// front/back/etc
        barcodeView.getBarcodeView().setCameraSettings(ca);
        barcodeView.resume();

        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                System.out.println("barcode result: " + result.toString());
                // do your thing with result
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                System.out.println(resultPoints.size());
            }
        });
    }

    private void upScanner(View view) {
        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.barcode_view);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.CODE_39); // Set barcode type
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(Objects.requireNonNull(getActivity()).getIntent());
        barcodeView.decodeContinuous(callback);

        Button btnFlash = view.findViewById(R.id.btn_flash);
        if (!hasFlash()) {
            btnFlash.setVisibility(View.GONE);
        }
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFlashlight();
            }
        });
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            Log.e(TAG, result.getText()); // QR/Barcode result
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private boolean hasFlash() {
        return Objects.requireNonNull(getActivity()).getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight() {
        if (isFlashOn) {
            isFlashOn = false;
            barcodeView.setTorchOff();
        } else {
            isFlashOn = true;
            barcodeView.setTorchOn();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}