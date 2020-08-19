package com.example.educacioncontinua.models;

import java.util.Date;
import java.util.List;

public class Curso {
    private Integer id;
    private String nombre;
    private Date fechaInicio;
    private Date fechaFin;
    private Integer cantidadParticipantes;
    private String tipoEduContinua;
    private String programaResponsable;
    private String docenteResponsable;
    private List<Jornada> jornadas = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getCantidadParticipantes() {
        return cantidadParticipantes;
    }

    public void setCantidadParticipantes(Integer cantidadParticipantes) {
        this.cantidadParticipantes = cantidadParticipantes;
    }

    public String getTipoEduContinua() {
        return tipoEduContinua;
    }

    public void setTipoEduContinua(String tipoEduContinua) {
        this.tipoEduContinua = tipoEduContinua;
    }

    public String getProgramaResponsable() {
        return programaResponsable;
    }

    public void setProgramaResponsable(String programaResponsable) {
        this.programaResponsable = programaResponsable;
    }

    public String getDocenteResponsable() {
        return docenteResponsable;
    }

    public void setDocenteResponsable(String docenteResponsable) {
        this.docenteResponsable = docenteResponsable;
    }

    public List<Jornada> getJornadas() {
        return jornadas;
    }

    public void setJornadas(List<Jornada> jornadas) {
        this.jornadas = jornadas;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", cantidadParticipantes=" + cantidadParticipantes +
                ", tipoEduContinua='" + tipoEduContinua + '\'' +
                ", programaResponsable='" + programaResponsable + '\'' +
                ", docenteResponsable='" + docenteResponsable + '\'' +
                ", jornadas=" + jornadas +
                '}';
    }
}
