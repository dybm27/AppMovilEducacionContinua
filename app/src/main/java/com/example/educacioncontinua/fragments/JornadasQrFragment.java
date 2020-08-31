package com.example.educacioncontinua.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.educacioncontinua.R;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.dagger.BaseApplication;
import com.example.educacioncontinua.interfaces.RetrofitApi;
import com.example.educacioncontinua.models.Jornada;
import com.example.educacioncontinua.models.RespuestaAsistencia;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JornadasQrFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<Jornada> jornadas;
    private ArrayList<String> arrayListJornadas = new ArrayList<>();

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private TextView textViewSinPermiso, textViewDocumento, textViewNombre,
            textViewTipoParticipante, textViewError, textViewTitulo, textViewSinResultado;
    private int idEduContinua, idJornada;
    private String qr;
    private AutoCompleteTextView editTextFilledExposedDropdown;
    private ProgressDialog progressDialog;
    private LinearLayoutCompat linearLayoutResultados;

    @Inject
    RetrofitApi retrofitApi;

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
        upView(view, datosRecuperados);
        if (verificarPermisoCamara()) {
            textViewSinPermiso.setVisibility(View.GONE);
            barcodeView.setVisibility(View.VISIBLE);
            upScanner(view);
        } else {
            textViewSinPermiso.setVisibility(View.VISIBLE);
            barcodeView.setVisibility(View.GONE);
        }
        llenarAdapter();
        setUpDagger();
        return view;
    }

    public void upView(View view, Bundle datosRecuperados) {
        idEduContinua = datosRecuperados.getInt("idEduContinua");
        jornadas = datosRecuperados.getParcelableArrayList("jornadas");
        editTextFilledExposedDropdown = view.findViewById(R.id.filled_exposed_dropdown);
        textViewSinPermiso = view.findViewById(R.id.textViewSinPermiso);
        textViewNombre = view.findViewById(R.id.textViewNombre);
        textViewTipoParticipante = view.findViewById(R.id.textViewTipoParticipante);
        textViewDocumento = view.findViewById(R.id.textViewDocumento);
        textViewError = view.findViewById(R.id.textViewError);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);
        textViewSinResultado = view.findViewById(R.id.textViewSinResultados);
        linearLayoutResultados = view.findViewById(R.id.linearLayoutResultados);
        textViewTitulo.setText(datosRecuperados.getString("nombreEvento"));
        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.barcode_scanner);
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.progress_dialog_jornadas);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private boolean verificarPermisoCamara() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void llenarAdapter() {
        for (int i = 0; i < jornadas.size(); i++) {
            arrayListJornadas.add(jornadas.get(i).getFechaJornadaString() + " - " + jornadas.get(i).getHoraInicioString());
        }
        llenarMenuDesplegable();
    }

    public void llenarMenuDesplegable() {
        Log.d("arrayJornadas", arrayListJornadas.get(0));
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        Objects.requireNonNull(getContext()),
                        R.layout.dropdown_menu_popup_item,
                        arrayListJornadas);
        editTextFilledExposedDropdown.setAdapter(adapter);
        editTextFilledExposedDropdown.setText(arrayListJornadas.get(0), false);
        obtenerIds(arrayListJornadas.get(0));
        editTextFilledExposedDropdown.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        obtenerIds(adapterView.getItemAtPosition(pos).toString());

    }

    public void obtenerIds(String seleccion) {
        for (int i = 0; i < jornadas.size(); i++) {
            String valor = jornadas.get(i).getFechaJornadaString() + " - " + jornadas.get(i).getHoraInicioString();
            if (valor.equals(seleccion)) {
                idJornada = jornadas.get(i).getId();
            }
        }
    }

    private void upScanner(View view) {
        beepManager = new BeepManager(Objects.requireNonNull(getActivity()));
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(Objects.requireNonNull(getActivity()).getIntent());
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() == null) {
                    return;
                }
                setUpProgressDialog();
                barcodeView.pause();
                lastText = result.getText();
                barcodeView.setStatusText(result.getText());
                beepManager.playBeepSoundAndVibrate();
                qr = result.getText();
                verificarAsistencia();
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
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

    private void setUpDagger() {
        ((BaseApplication) Objects.requireNonNull(getActivity()).getApplication()).getRetrofitComponent().inject(this);
    }

    private void verificarAsistencia() {
        Call<RespuestaAsistencia> call = retrofitApi.asistencia(idEduContinua, idJornada, qr);
        call.enqueue(new Callback<RespuestaAsistencia>() {
            @Override
            public void onResponse(Call<RespuestaAsistencia> call, Response<RespuestaAsistencia> response) {
                barcodeView.resume();
                progressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        RespuestaAsistencia respuestaAsistencia = response.body();
                        assert respuestaAsistencia != null;
                        textViewError.setVisibility(View.GONE);
                        textViewSinResultado.setVisibility(View.GONE);
                        textViewNombre.setText(respuestaAsistencia.getNombre());
                        textViewTipoParticipante.setText(respuestaAsistencia.getTipoParticipante());
                        textViewDocumento.setText(respuestaAsistencia.getDocumento());
                        linearLayoutResultados.setVisibility(View.VISIBLE);
                        ToastrConfig.mensaje(getContext(), "Asistencia Registrada");
                    } else {
                        textViewError.setVisibility(View.VISIBLE);
                        linearLayoutResultados.setVisibility(View.GONE);
                        textViewSinResultado.setVisibility(View.GONE);
                        mensajeError(response.code());
                    }
                } catch (Exception ex) {
                    textViewError.setVisibility(View.VISIBLE);
                    linearLayoutResultados.setVisibility(View.GONE);
                    textViewSinResultado.setVisibility(View.GONE);
                    ToastrConfig.mensaje(getContext(), "Error tipografico");
                }
            }

            @Override
            public void onFailure(Call<RespuestaAsistencia> call, Throwable t) {
                barcodeView.resume();
                progressDialog.dismiss();
                textViewError.setVisibility(View.VISIBLE);
                linearLayoutResultados.setVisibility(View.GONE);
                textViewSinResultado.setVisibility(View.GONE);
                ToastrConfig.mensaje(getContext(), "Grave Error");
            }
        });
    }

    private void mensajeError(int code) {
        switch (code) {
            case 400:
                ToastrConfig.mensaje(getContext(), "No Se Encontro La Jornada");
                break;
            case 409:
                ToastrConfig.mensaje(getContext(), "La Jornada No Existe");
                break;
            case 412:
                ToastrConfig.mensaje(getContext(), "El Participante No se Encuentra Inscrito");
                break;
            case 500:
                ToastrConfig.mensaje(getContext(), "Qr Invalido");
                break;
        }
    }
}