package com.example.educacioncontinua.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("primerNombre")
    val firstName: String,
    @SerializedName("segundoNombre")
    val secondName: String?,
    @SerializedName("primerApellido")
    val surname: String,
    @SerializedName("segundoApellido")
    val secondSurname: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("telefono")
    val phone: String,
    @SerializedName("estudiante")
    val student: Boolean,
    @SerializedName("docente")
    val professor: Boolean,
    @SerializedName("administrativo")
    val administrative: Boolean,
    @SerializedName("graduado")
    val graduate: Boolean,
    @SerializedName("externo")
    val external: Boolean
) : Parcelable