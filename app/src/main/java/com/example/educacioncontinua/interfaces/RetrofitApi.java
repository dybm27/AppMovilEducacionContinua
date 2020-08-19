package com.example.educacioncontinua.interfaces;

import com.example.educacioncontinua.models.Curso;
import com.example.educacioncontinua.models.Jornada;
import com.example.educacioncontinua.models.Token;
import com.example.educacioncontinua.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitApi {
    @GET("validateLogin/{email}")
    Call<Usuario> verificarUser(@Path("email") String email);

    @GET("misCursosYEduContinua/{idUser}")
    Call<List<Curso>> obtenerCursos(@Path("idUser") int idUser);

    @POST("api/token/{token}")
    Call<Usuario> verificarToken(@Body Token token);
}
