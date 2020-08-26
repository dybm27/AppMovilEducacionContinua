package com.example.educacioncontinua.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class Curso implements Parcelable {
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

    protected Curso(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        nombre = in.readString();
        fechaInicio = new Date(in.readLong());
        fechaFin = new Date(in.readLong());
        if (in.readByte() == 0) {
            cantidadParticipantes = null;
        } else {
            cantidadParticipantes = in.readInt();
        }
        tipoEduContinua = in.readString();
        programaResponsable = in.readString();
        docenteResponsable = in.readString();
        jornadas = in.createTypedArrayList(Jornada.CREATOR);
    }

    public static final Creator<Curso> CREATOR = new Creator<Curso>() {
        @Override
        public Curso createFromParcel(Parcel in) {
            return new Curso(in);
        }

        @Override
        public Curso[] newArray(int size) {
            return new Curso[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(nombre);
        parcel.writeLong(fechaInicio.getTime());
        parcel.writeLong(fechaFin.getTime());
        if (cantidadParticipantes == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(cantidadParticipantes);
        }
        parcel.writeString(tipoEduContinua);
        parcel.writeString(programaResponsable);
        parcel.writeString(docenteResponsable);
        parcel.writeTypedList(jornadas);
    }
}
