package com.example.educacioncontinua.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Jornada implements Parcelable {
    private Integer id;
    private Date horaInicio;
    private Date horaFin;
    private Integer idEducacionContinua;
    private Date fechaInicioEduContinua;
    private Date fechaFinEduContinua;
    private String fechaJornadaString;
    private String horaInicioString;
    private String horaFinString;

    public Jornada() {

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Date horaFin) {
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

    public String getHoraInicioString() {
        return horaInicioString;
    }

    public void setHoraInicioString(String horaInicioString) {
        this.horaInicioString = horaInicioString;
    }

    public String getHoraFinString() {
        return horaFinString;
    }

    public void setHoraFinString(String horaFinString) {
        this.horaFinString = horaFinString;
    }

    public String getFechaJornadaString() {
        return fechaJornadaString;
    }

    public void setFechaJornadaString(String fechaJornadaString) {
        this.fechaJornadaString = fechaJornadaString;
    }

    public Jornada(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        horaInicio = new Date(in.readLong());
        horaFin = new Date(in.readLong());
        if (in.readByte() == 0) {
            idEducacionContinua = null;
        } else {
            idEducacionContinua = in.readInt();
        }
        fechaInicioEduContinua = new Date(in.readLong());
        fechaFinEduContinua = new Date(in.readLong());
        fechaJornadaString = in.readString();
        horaInicioString = in.readString();
        horaFinString = in.readString();
    }

    public static final Creator<Jornada> CREATOR = new Creator<Jornada>() {
        @Override
        public Jornada createFromParcel(Parcel in) {
            return new Jornada(in);
        }

        @Override
        public Jornada[] newArray(int size) {
            return new Jornada[size];
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
        parcel.writeLong(horaInicio.getTime());
        parcel.writeLong(horaFin.getTime());
        if (idEducacionContinua == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeLong(fechaInicioEduContinua.getTime());
        parcel.writeLong(fechaFinEduContinua.getTime());
        parcel.writeString(fechaJornadaString);
        parcel.writeString(horaInicioString);
        parcel.writeString(horaFinString);

    }
}
