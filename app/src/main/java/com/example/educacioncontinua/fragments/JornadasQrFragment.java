package com.example.educacioncontinua.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private TextView textViewSinPermiso;
    private int idEduContinua, idJornada;
    private String qr;
    private AutoCompleteTextView editTextFilledExposedDropdown;
    private Dialog dialogVerificando;

    // modal exito
    private TextView textViewNombre, textViewTipo, textViewDocumento;
    private Button btnModalExito;
    // modal error
    private TextView textViewError;
    private Button btnModalError;
    private AlertDialog modalExito, modalError;

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
        setUpProgressVerificar();
        setUpModalExito();
        setUpModalError();
        return view;
    }

    public void upView(View view, Bundle datosRecuperados) {
        idEduContinua = datosRecuperados.getInt("idEduContinua");
        jornadas = datosRecuperados.getParcelableArrayList("jornadas");
        editTextFilledExposedDropdown = view.findViewById(R.id.filled_exposed_dropdown);
        textViewSinPermiso = view.findViewById(R.id.textViewSinPermiso);
        TextView textViewTitulo = view.findViewById(R.id.textViewTitulo);
        textViewTitulo.setText(datosRecuperados.getString("nombreEvento"));
        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.barcode_scanner);
    }

    private void setUpProgressVerificar() {
        dialogVerificando = new Dialog(Objects.requireNonNull(getContext()));
        dialogVerificando.setCancelable(false);
        dialogVerificando.setCanceledOnTouchOutside(false);
        dialogVerificando.setContentView(R.layout.progress_bar_jornadas);
        dialogVerificando.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void setUpModalExito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_jornadas_exito, null);
        builder.setView(view);
        modalExito = builder.create();
        modalExito.setCancelable(false);
        modalExito.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(modalExito.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        textViewNombre = view.findViewById(R.id.textViewNombreModal);
        textViewTipo = view.findViewById(R.id.textViewTipoModal);
        textViewDocumento = view.findViewById(R.id.textViewDocumentoModal);
        btnModalExito = view.findViewById(R.id.btnModalExito);
        btnModalExito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalExito.dismiss();
                resume();
            }
        });
    }

    private void abrirModalExito(RespuestaAsistencia respuestaAsistencia) {
        modalExito.show();
        SpannableString text1 = new SpannableString(respuestaAsistencia.getNombre());
        text1.setSpan(new UnderlineSpan(), 0, text1.length(), 0);
        textViewNombre.setText(text1);
        SpannableString text2 = new SpannableString(respuestaAsistencia.getTipoParticipante());
        text2.setSpan(new UnderlineSpan(), 0, text2.length(), 0);
        textViewTipo.setText(text2);
        SpannableString text3 = new SpannableString(respuestaAsistencia.getDocumento());
        text3.setSpan(new UnderlineSpan(), 0, text3.length(), 0);
        Log.e("error", respuestaAsistencia.getDocumento());
        textViewDocumento.setText(text3);
    }

    private void setUpModalError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_jornadas_error, null);
        builder.setView(view);
        modalError = builder.create();
        modalError.setCancelable(false);
        modalError.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(modalError.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        textViewError = view.findViewById(R.id.textViewErrorModal);
        Button btnSalir = view.findViewById(R.id.btnModalError);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalError.dismiss();
                resume();
            }
        });
    }

    private void abrirModalError(String mensajeError) {
        modalError.show();
        textViewError.setText(mensajeError);
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
        barcodeView.setStatusText("");
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(Objects.requireNonNull(getActivity()).getIntent());
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() == null) {
                    return;
                }
                beepManager.playBeepSoundAndVibrate();
                if (lastText != null) {
                    if (lastText.equals(result.getText())) {
                        abrirModalError("El Qr ya fue leído con éxito");
                        return;
                    }
                }
                dialogVerificando.show();
                pause();
                qr = result.getText();
                verificarAsistencia(result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
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

    private void verificarAsistencia(String resulQr) {
        Call<RespuestaAsistencia> call = retrofitApi.asistencia(idEduContinua, idJornada, qr);
        call.enqueue(new Callback<RespuestaAsistencia>() {
            @Override
            public void onResponse(Call<RespuestaAsistencia> call, Response<RespuestaAsistencia> response) {
                dialogVerificando.dismiss();
                try {
                    if (response.isSuccessful()) {
                        lastText = resulQr;
                        RespuestaAsistencia respuestaAsistencia = response.body();
                        assert respuestaAsistencia != null;
                        abrirModalExito(respuestaAsistencia);
                        ToastrConfig.mensaje(getContext(), "Asistencia Registrada");
                    } else {
                        lastText = null;
                        abrirModalError(mensajeError(response.code()));
                    }
                } catch (Exception ex) {
                    lastText = null;
                    Log.e("Errorrrrrr =", Objects.requireNonNull(ex.getMessage()));
                    ToastrConfig.mensaje(getContext(), "Error tipografico");
                }
            }

            @Override
            public void onFailure(Call<RespuestaAsistencia> call, Throwable t) {
                lastText = null;
                resume();
                dialogVerificando.dismiss();
                ToastrConfig.mensaje(getContext(), "La peticion fallo.. vuelva a intentarlo");
            }
        });
    }

    private String mensajeError(int code) {
        String mensaje = "";
        switch (code) {
            case 400:
                mensaje = "No se encontro la jornada.";
                break;
            case 409:
                mensaje = "La asistecia ya fue registrada.";
                break;
            case 412:
                mensaje = "El participante no se encuentra inscrito.";
                break;
            case 500:
                mensaje = "El Qr es invalido.";
                break;
        }
        return mensaje;
    }
}