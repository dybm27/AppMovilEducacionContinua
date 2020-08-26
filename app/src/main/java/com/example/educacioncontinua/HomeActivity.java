package com.example.educacioncontinua;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.educacioncontinua.adapter.CursoAdapter;
import com.example.educacioncontinua.config.GoogleSingInService;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.dagger.BaseApplication;
import com.example.educacioncontinua.fragments.CursosFragment;
import com.example.educacioncontinua.fragments.JornadasQrFragment;
import com.example.educacioncontinua.interfaces.RetrofitApi;
import com.example.educacioncontinua.models.Curso;
import com.example.educacioncontinua.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    private BottomAppBar bottomAppBar;
    private FloatingActionButton floatingActionButton;


    private FragmentTransaction fragmentTransaction;
    private Fragment fragmentCursos;
    private ProgressDialog progressDialog;

    @Inject
    RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpProgressDialog();
        //setUpBottomAppBar();
        setUpDagger();
        obtenerCursos();
        floatingActionButton = findViewById(R.id.floatActionButton);
        floatingActionButton.setOnClickListener(this);
    }

    private void setUpDagger() {
        ((BaseApplication) getApplication()).getRetrofitComponent().inject(this);
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        //progressDialog.setIcon(R.mipmap.ic_launcher);
        //progressDialog.setMessage("Cargando...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatActionButton:
                signOut();
                break;
        }
    }

    private void setUpBottomAppBar() {
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        ToastrConfig.mensaje(HomeActivity.this, "buscar");
                        break;
                }
                return false;
            }
        });
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("setNavigationOnClickListener");
            }
        });
    }


    private void signOut() {
        GoogleSingInService.getMGoogleSignInClient(this).signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Usuario.cerrarSesion();
                        redirectLogin();
                    }
                });
    }

    public void redirectLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void obtenerCursos() {
        Call<List<Curso>> call = retrofitApi.obtenerCursos(Usuario.getUsuario().getId());
        call.enqueue(new Callback<List<Curso>>() {
            @Override
            public void onResponse(Call<List<Curso>> call, Response<List<Curso>> response) {
                progressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        ToastrConfig.mensaje(HomeActivity.this, "OK");
                        abrirFragmento(response.body());
                    } else {
                        mensajeError();
                      //  ToastrConfig.mensaje(HomeActivity.this, "Error server");
                    }
                } catch (Exception ex) {
                    mensajeError();
                    //ToastrConfig.mensaje(HomeActivity.this, "Error tipografico");
                }
            }

            @Override
            public void onFailure(Call<List<Curso>> call, Throwable t) {
                progressDialog.dismiss();
                mensajeError();
            }
        });
    }

    public void abrirFragmento(List<Curso> cursos) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("cursos", (ArrayList<? extends Parcelable>) cursos);
        fragmentCursos = new CursosFragment();
        fragmentCursos.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment, fragmentCursos).commit();
    }

    public void mensajeError() {
        ToastrConfig.mensaje(HomeActivity.this, "No fue posible obtener la informacion.. Ingrese nuevamente");
        signOut();
    }
}
