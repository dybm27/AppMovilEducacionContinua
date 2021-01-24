package com.example.educacioncontinua;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.educacioncontinua.config.GoogleSingInService;
import com.example.educacioncontinua.dagger.BaseApplication;
import com.example.educacioncontinua.interfaces.RetrofitApi;
import com.example.educacioncontinua.models.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000;

    //variables
    private Animation topAnimation, bottomAnimation, lestAnimation;
    private ImageView buttomSplash, logoUfps;

    private Handler handler;
    private Runnable runnable;
    @Inject
    RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        setUpDagger();
        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        lestAnimation = AnimationUtils.loadAnimation(this, R.anim.left_animation);

        buttomSplash = findViewById(R.id.iv_splash_buttom);
        logoUfps = findViewById(R.id.iv_logo_ufps);

        buttomSplash.setAnimation(bottomAnimation);
        logoUfps.setAnimation(lestAnimation);
    }

    private void setUpDagger() {
        ((BaseApplication) getApplication()).getRetrofitComponent().inject(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        verificarUsuario(account);
    }

    private void verificarUsuario(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            Call<Usuario> call = retrofitApi.verificarUser(account.getIdToken());
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    try {
                        if (response.isSuccessful()) {
                            Usuario.setUsuario(response.body());
                            abrirActivityHome();
                        } else {
                            revokeAccess();
                        }
                    } catch (Exception ex) {
                        revokeAccess();
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    revokeAccess();
                }
            });
        } else {
            continuarSplash();
        }
    }

    private void continuarSplash() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(logoUfps, "logo_splash");
                pairs[1] = new Pair<View, String>(buttomSplash, "buttom_splash");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                startActivity(intent, options.toBundle());
                finish();
            }
        };
        handler.postDelayed(runnable, SPLASH_SCREEN);
    }

    private void revokeAccess() {
        GoogleSingInService.getMGoogleSignInClient(this).revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        continuarSplash();
                    }
                });
    }

    public void abrirActivityHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        finish();
    }
}