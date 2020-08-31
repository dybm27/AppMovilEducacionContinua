package com.example.educacioncontinua;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2500;

    //variables
    private Animation topAnimation, bottomAnimation, lestAnimation;
    private ImageView logoApp, logoUfps;
    private TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        lestAnimation = AnimationUtils.loadAnimation(this, R.anim.left_animation);

        logoApp = findViewById(R.id.imageViewLogoApp);
        logoUfps = findViewById(R.id.imageViewLogoUfps);
        titulo = findViewById(R.id.textViewTitulo);

        logoApp.setAnimation(topAnimation);
        logoUfps.setAnimation(lestAnimation);
        titulo.setAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(logoApp, "logo_splash");
                pairs[1] = new Pair<View, String>(titulo, "text_splash");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                startActivity(intent, options.toBundle());
                //finish();
            }
        }, SPLASH_SCREEN);
    }
}