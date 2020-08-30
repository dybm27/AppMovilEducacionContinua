package com.example.educacioncontinua.models;

public class RespuestaAsistencia {
    private String nombre;
    private String documento;
    private String tipoDocumento;
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

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipo(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTipoParticipante() {
        return tipoParticipante;
    }

    public void setParticipante(String tipoParticipante) {
        this.tipoParticipante = tipoParticipante;
    }
}
