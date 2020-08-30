package com.example.educacioncontinua.fragments;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.educacioncontinua.R;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.models.Jornada;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
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
    private ArrayList<String> arrayListJornadas = new ArrayList<>();

    private static final String TAG = "ScanQRActivity";
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private Button btnPuase;
    private Button btnResume;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                Log.d("negativo", lastText);
                return;
            }
            Log.d("funciono", result.getText());
            lastText = result.getText();
            barcodeView.setStatusText(result.getText());

            beepManager.playBeepSoundAndVibrate();

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            System.out.println(resultPoints.size());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jornadas_qr, container, false);
        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            ToastrConfig.mensaje(getContext(), "No se pudieron cargar los datos");
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }
        assert datosRecuperados != null;
        jornadas = datosRecuperados.getParcelableArrayList("jornadas");
        upScanner(view);
        llenarAdapter(view);
        return view;
    }

    public void llenarAdapter(View view) {
        for (int i = 0; i < jornadas.size(); i++) {
            arrayListJornadas.add(jornadas.get(i).getFechaJornadaString() + " - " + jornadas.get(i).getHoraInicioString());
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

    private void upScanner(View view) {
        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(Objects.requireNonNull(getActivity()).getIntent());
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(Objects.requireNonNull(getActivity()));

        btnPuase = view.findViewById(R.id.btnPause);
        btnPuase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause();
            }
        });
        btnResume = view.findViewById(R.id.btnResume);
        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resume();
            }
        });
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

    public void pause() {
        barcodeView.pause();
    }

    public void resume() {
        barcodeView.resume();
    }

}