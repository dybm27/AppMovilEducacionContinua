package com.example.educacioncontinua.dagger;

import com.example.educacioncontinua.HomeActivity;
import com.example.educacioncontinua.MainActivity;
import com.example.educacioncontinua.fragments.CursosFragment;
import com.example.educacioncontinua.fragments.JornadasQrFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RetrofitModule.class)
public interface RetrofitComponent {
    void inject(MainActivity mainActivity);

    void inject(HomeActivity homeActivity);

    void inject(CursosFragment cursosFragment);

    void inject(JornadasQrFragment jornadasQrFragment);
}
