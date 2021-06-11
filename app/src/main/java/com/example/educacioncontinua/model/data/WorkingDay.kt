package com.example.educacioncontinua.model.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class WorkingDay(
    @SerializedName("id")
    val id: Int,
    @SerializedName("fechaJornadaString")
    val dayDateString: String,
    @SerializedName("horaInicioString")
    val startTimeString: String,
    @SerializedName("horaFinString")
    val endTimeString: String,
    @SerializedName("horaInicio")
    val startTime: Date,
    @SerializedName("horaFin")
    val endTime: Date
) : Parcelable
