package com.example.educacioncontinua;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.educacioncontinua.config.GoogleSingInService;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.dagger.BaseApplication;
import com.example.educacioncontinua.interfaces.RetrofitApi;
import com.example.educacioncontinua.models.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;

    @Inject
    RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpDagger();
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
    }

    private void setUpDagger() {
        ((BaseApplication) getApplication()).getRetrofitComponent().inject(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        verificarUsuario(account);
    }

    private void verificarUsuario(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            System.out.println("token: " + account.getIdToken());
            Call<Usuario> call = retrofitApi.verificarUser(account.getIdToken());
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    try {
                        if (response.isSuccessful()) {
                            Usuario.setUsuario(response.body());
                            ToastrConfig.mensaje(MainActivity.this, "Logueado");
                            abrirActivityHome();
                        } else {
                            revokeAccess(response.code());
                        }
                    } catch (Exception ex) {
                        ToastrConfig.mensaje(MainActivity.this, "Error en el Servidor");
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    ToastrConfig.mensaje(MainActivity.this, "Error de conexión");
                }
            });
        }
    }

    private void signIn() {
        Intent signInIntent = GoogleSingInService.getMGoogleSignInClient(this).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            verificarUsuario(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            verificarUsuario(null);
        }
    }

    private void revokeAccess(int code) {
        GoogleSingInService.getMGoogleSignInClient(this).revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        switch (code) {
                            case 403:
                                ToastrConfig.mensaje(MainActivity.this, "No tienes los permisos necesarios para ingresar");
                                break;
                            case 500:
                                ToastrConfig.mensaje(MainActivity.this, "No te encuentras registrado/a");
                                break;
                            case 400:
                                ToastrConfig.mensaje(MainActivity.this, "Su Token de validación no es valida");
                                break;
                        }
                    }
                });
    }

    public void abrirActivityHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}