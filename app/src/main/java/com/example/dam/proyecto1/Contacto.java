package com.example.dam.proyecto1;


import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Contacto implements Serializable,Comparable<Contacto>,Parcelable {

    private long id;
    private String nombre;
    private List<String> tlf;

    /****************************************Constructores***************************************/
    public Contacto(String nombre, long id, List<String> tlf) {
        this.nombre = nombre;
        this.id = id;
        this.tlf = tlf;
    }

    public Contacto(){
        this("",0,new ArrayList<String>());
    }

    /************************************Getters and Setters**************************************/
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstTlf() {
        return tlf.get(0);
    }

    public String getSelectedTlf(int pos){return tlf.get(pos);}

    public String getTlf() {
        String s="";
        for(String a:tlf)
            s+=a+"\n";
        return s;
    }

    public int getSize() {
        return tlf.size();
    }

    public void setTlf(List<String> tlf) {
        this.tlf=tlf;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    /*@Override*/
    /*public String toString() {
        return "Contacto{" +
                "nombre='" + nombre + '\'' +
                ", id=" + id +
                ", tlf=" + tlf +
                '}';
    }*/

    /****************************************Comparadores*****************************************/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;

        return id == contacto.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public int compareTo(Contacto contacto) {
        int r=this.nombre.compareTo(contacto.nombre);
        if(r==0){
            r= (int) (this.id - contacto.id);
        }
        return r;
    }

    /*******************************************Parcelable*****************************************/

    // Metodos necesarios para definir Parcelables que he necesitado a la hora de pasar un Contacto
    // entre actividades

    public static final Parcelable.Creator<Contacto> CREATOR = new Parcelable.Creator<Contacto>() {
        @Override
        public Contacto createFromParcel(Parcel in) {
            return new Contacto(in);
        }

        @Override
        public Contacto[] newArray(int size) {
            return new Contacto[size];
        }
    };

    //Constuctor del Parcelable
    public Contacto(Parcel parcel){
        nombre= new String();
        tlf=new ArrayList<>();
        readToParcel(parcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Insertar y leer del parcelable
    public void writeToParcel(Parcel parcel,int pos){
        parcel.writeString(nombre);
        parcel.writeLong(id);
        parcel.writeStringList(tlf);
    }

    public void readToParcel(Parcel parcel){
        nombre=parcel.readString();
        id= parcel.readLong();
        parcel.readStringList(tlf);
    }
}
