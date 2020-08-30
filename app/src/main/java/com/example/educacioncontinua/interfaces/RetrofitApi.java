package com.example.educacioncontinua.interfaces;

import com.example.educacioncontinua.models.Curso;
import com.example.educacioncontinua.models.RespuestaAsistencia;
import com.example.educacioncontinua.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitApi {
    @GET("validateLogin/{token}")
    Call<Usuario> verificarUser(@Path("token") String token);

    @GET("misCursosYEduContinua/{idUser}")
    Call<List<Curso>> obtenerCursos(@Path("idUser") int idUser);

    @GET("misCursosYEduContinua/{idEdu}/{idJornada}/{qr}")
    Call<RespuestaAsistencia> asistencia(@Path("idEdu") int idEdu, @Path("idJornada") int idJornada, @Path("qr") String qr);
}
