package com.example.educacioncontinua.config;

import android.content.Context;
import android.widget.Toast;

public class ToastrConfig {
    public static void mensaje(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }
}
