package com.example.educacioncontinua.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class BackgroundService extends IntentService {
    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
       /* if (intent != null) {
            String email = intent.getStringExtra("email");
            if (email != null) {
                System.out.println(email);
                UsuarioApi usuarioApi = RetrofitConfig.getRetrofit().create(UsuarioApi.class);
                Call<Usuario> call = usuarioApi.verificarUser(email);
                try {
                    Response<Usuario> response = call.execute();
                    System.out.println("----------------------------response");
                    System.out.println(response.body());
                } catch (IOException e) {
                    System.out.println("----------------------------error");
                    System.out.println(e.getMessage());
                }
            }
        }*/
    }
}
