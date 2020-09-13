package com.example.educacioncontinua.models;

import com.google.gson.annotations.SerializedName;

public class RespuestaAsistencia {
    @SerializedName("nombrePersona")
    private String nombre;
    @SerializedName("numeroDocumento")
    private String documento;
    private String tipoParticipante;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTipoParticipante() {
        return tipoParticipante;
    }

    public void setParticipante(String tipoParticipante) {
        this.tipoParticipante = tipoParticipante;
    }
}
