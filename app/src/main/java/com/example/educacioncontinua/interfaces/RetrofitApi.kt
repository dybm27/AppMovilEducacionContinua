package com.example.educacioncontinua.interfaces

import com.example.educacioncontinua.models.AssistanceResponse
import com.example.educacioncontinua.models.Course
import com.example.educacioncontinua.models.User
import com.example.educacioncontinua.models.WorkingDay
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitApi {
    @GET("validateLogin/{token}")
    fun verifyUser(@Path("token") token: String?): Call<User>

    @GET("misCursosYEduContinua/{idUser}")
    fun getCourses(@Path("idUser") idUser: Int): Call<List<Course>>

    @GET("jornadasEducacionContinua/{idEdu}")
    fun getJourneys(@Path("idEdu") idEdu: Int): Call<List<WorkingDay>>

    @GET("asistencia/{idEdu}/{idJornada}/{qr}")
    fun assistance(
        @Path("idEdu") idEdu: Int,
        @Path("idJornada") idJourney: Int,
        @Path("qr") qr: String
    ): Call<AssistanceResponse>
}