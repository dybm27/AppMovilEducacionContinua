package com.example.educacioncontinua.models;

import java.util.Date;

public class Jornada {
    private Integer id;
    private String horaInicio;
    private String horaFin;
    private Integer idEducacionContinua;
    private Date fechaInicioEduContinua;
    private Date fechaFinEduContinua;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public Integer getIdEducacionContinua() {
        return idEducacionContinua;
    }

    public void setIdEducacionContinua(Integer idEducacionContinua) {
        this.idEducacionContinua = idEducacionContinua;
    }

    public Date getFechaInicioEduContinua() {
        return fechaInicioEduContinua;
    }

    public void setFechaInicioEduContinua(Date fechaInicioEduContinua) {
        this.fechaInicioEduContinua = fechaInicioEduContinua;
    }

    public Date getFechaFinEduContinua() {
        return fechaFinEduContinua;
    }

    public void setFechaFinEduContinua(Date fechaFinEduContinua) {
        this.fechaFinEduContinua = fechaFinEduContinua;
    }
}
