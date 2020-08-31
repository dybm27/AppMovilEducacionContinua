package com.example.educacioncontinua.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Jornada implements Parcelable {
    private Integer id;
    private String fechaJornadaString;
    private String horaInicioString;
    private String horaFinString;
    private Date horaInicio;
    private Date horaFin;


    public Jornada() {

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        fechaJornadaString = in.readString();
        horaInicioString = in.readString();
        horaFinString = in.readString();
        horaInicio = new Date(in.readLong());
        horaFin = new Date(in.readLong());
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
        parcel.writeString(fechaJornadaString);
        parcel.writeString(horaInicioString);
        parcel.writeString(horaFinString);
        parcel.writeLong(horaInicio.getTime());
        parcel.writeLong(horaFin.getTime());

    }
}
