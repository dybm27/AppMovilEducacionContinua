package com.example.educacioncontinua.config;

import android.content.Context;
import android.content.res.Resources;

import com.example.educacioncontinua.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleSingInService {

    private Context context;
    private static GoogleSignInClient mGoogleSignInClient;

    public static GoogleSignInClient getMGoogleSignInClient(Context context) {
        if (mGoogleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    //.requestIdToken(context.getResources().getString(R.string.server_client_id))
                    .requestEmail()
                    .build();
            System.out.println(context.getResources().getString(R.string.server_client_id));
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        }
        return mGoogleSignInClient;
    }

}
