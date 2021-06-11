package com.example.educacioncontinua.model

import com.example.educacioncontinua.model.data.Assistance
import com.example.educacioncontinua.model.data.Course
import com.example.educacioncontinua.model.data.User
import com.example.educacioncontinua.model.data.WorkingDay
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitApi {
    @GET("validateLogin/{token}")
    suspend fun verifyUser(@Path("token") token: String?): Response<User>

    @GET("misCursosYEduContinua/{idUser}")
    suspend fun getCourses(@Path("idUser") idUser: Int): Response<List<Course>>

    @GET("jornadasEducacionContinua/{idEdu}")
    suspend fun getWorkingDays(@Path("idEdu") idEdu: Int): Response<List<WorkingDay>>

    @GET("asistencia/{idEdu}/{idJornada}/{qr}")
    suspend fun assistance(
        @Path("idEdu") idEdu: Int,
        @Path("idJornada") idWorkingDay: Int,
        @Path("qr") qr: String
    ): Response<Assistance>
}