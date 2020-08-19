package com.example.educacioncontinua;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.educacioncontinua.config.GoogleSingInService;
import com.example.educacioncontinua.config.RetrofitConfig;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.interfaces.RetrofitApi;
import com.example.educacioncontinua.models.Curso;
import com.example.educacioncontinua.models.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logoutButton;
    private TextView textViewHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        obtenerCursos();
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(this);
        textViewHome = findViewById(R.id.textViewHome);
        textViewHome.setText(Usuario.getUsuario().getEmail());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_button:
                signOut();
                break;
        }
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
        RetrofitApi retrofitApi = RetrofitConfig.getRetrofit().create(RetrofitApi.class);
        Call<List<Curso>> call = retrofitApi.obtenerCursos(Usuario.getUsuario().getId());
        call.enqueue(new Callback<List<Curso>>() {
            @Override
            public void onResponse(Call<List<Curso>> call, Response<List<Curso>> response) {
                try {
                    if (response.isSuccessful()) {
                        ToastrConfig.mensaje(HomeActivity.this, "OK");
                        List<Curso> cursos = response.body();
                        for (Curso cu : cursos) {
                            System.out.println(cu.toString());
                        }
                    } else {
                        System.out.println("---------------");
                        System.out.println(response.code());
                        System.out.println(response.errorBody());
                        System.out.println("---------------");
                        ToastrConfig.mensaje(HomeActivity.this, "Error server");
                    }
                } catch (Exception ex) {
                    ToastrConfig.mensaje(HomeActivity.this, "Error tipografico");
                }
            }

            @Override
            public void onFailure(Call<List<Curso>> call, Throwable t) {
            }
        });
    }
}
