package com.example.educacioncontinua.interfaces;

import com.example.educacioncontinua.models.Curso;
import com.example.educacioncontinua.models.Jornada;
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

    @GET("jornadasEducacionContinua/{idEdu}")
    Call<List<Jornada>> obtenerJornadas(@Path("idEdu") int idEdu);

    @GET("asistencia/{idEdu}/{idJornada}/{qr}")
    Call<RespuestaAsistencia> asistencia(@Path("idEdu") int idEdu, @Path("idJornada") int idJornada, @Path("qr") String qr);
}
