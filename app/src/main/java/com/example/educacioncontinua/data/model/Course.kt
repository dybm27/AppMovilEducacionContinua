package com.example.educacioncontinua.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Course(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nombre")
    val name: String,
    @SerializedName("fechaInicio")
    val startDate: Date,
    @SerializedName("fechaFin")
    val endDate: Date,
    @SerializedName("cantidadParticipantes")
    val participantsQuantity: Int,
    @SerializedName("tipoEduContinua")
    val typeContinuingEducation: String,
    @SerializedName("programaResponsable")
    val responsibleProgram: String,
    @SerializedName("docenteResponsable")
    val responsibleProfessor: String
) : Parcelable
