package com.ufps.geduco.data

import com.ufps.geduco.data.model.Assistance
import com.ufps.geduco.data.model.Course
import com.ufps.geduco.data.model.User
import com.ufps.geduco.data.model.WorkingDay
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