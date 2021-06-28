package com.ufps.geduco.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Assistance(
    @SerializedName("nombrePersona")
    val name: String,
    @SerializedName("numeroDocumento")
    val document: String,
    @SerializedName("tipoParticipante")
    val participantType: String
) : Parcelable