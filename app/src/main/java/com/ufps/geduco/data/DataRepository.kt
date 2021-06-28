package com.ufps.geduco.data

import android.util.Log
import com.ufps.geduco.data.model.*
import javax.inject.Inject

class DataRepository @Inject constructor(private val retrofitApi: RetrofitApi) {

    companion object {
        const val TAG = "DataRepository"
        const val MSG_WITHOUT_CONNECTION =
            "No fue posible obtener la informacion.\nVerifique la conexión a internet"
        const val MSG_SERVER_ERROR = "Error en el servidor"
    }

    suspend fun verifyUser(token: String?): DataResponse<User> {
        var user: User? = null
        var message: String? = null
        try {
            val response = retrofitApi.verifyUser(token)
            user = response.body()
            if (validateResponseCode(response.code())) {
                message = getMessageVerifyUser(response.code())
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
            message = "Error en el servidor"
        }
        return DataResponse(user, message)
    }

    private fun getMessageVerifyUser(code: Int): String = when (code) {
        403 -> "No tienes los permisos necesarios para ingresar"
        500 -> "No te encuentras registrado/a"
        401 -> "Su token de validación no es valido"
        else -> "Error desconocido"
    }

    suspend fun getCourses(idUser: Int): DataResponse<List<Course>> {
        var message: String? = null
        var list = emptyList<Course>()
        try {
            val response = retrofitApi.getCourses(idUser)
            list = response.body() ?: emptyList()
            if (validateResponseCode(response.code())) {
                message = MSG_WITHOUT_CONNECTION
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
            message = MSG_SERVER_ERROR
        }
        return DataResponse(list, message)
    }

    suspend fun getWorkingDays(idEdu: Int): DataResponse<List<WorkingDay>> {
        var message: String? = null
        var list = emptyList<WorkingDay>()
        try {
            val response = retrofitApi.getWorkingDays(idEdu)
            list = response.body() ?: emptyList()
            if (validateResponseCode(response.code())) {
                message = MSG_WITHOUT_CONNECTION
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
            message = MSG_SERVER_ERROR
        }
        return DataResponse(list, message)
    }

    suspend fun checkAttendance(
        idEdu: Int,
        idWorkingDay: Int,
        qr: String
    ): DataResponse<Assistance> {
        var assistance: Assistance? = null
        var message: String? = null
        try {
            val response = retrofitApi.assistance(idEdu,idWorkingDay,qr)
            assistance = response.body()
            if (validateResponseCode(response.code())) {
                message = getMessageCheckAttendance(response.code())
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
            message = "Error en el servidor"
        }
        return DataResponse(assistance, message)
    }

    private fun getMessageCheckAttendance(code: Int): String = when (code) {
        400 -> "No se encontro la jornada."
        409 -> "La asistecia ya fue registrada."
        412 -> "El participante no se encuentra inscrito."
        500 -> "El Qr es invalido."
        else -> "Error no identificado"
    }

    private fun validateResponseCode(code: Int): Boolean = 200 != code
}