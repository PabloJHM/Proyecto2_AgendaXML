package com.example.dam.Proyecto2_CopiaXML;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AgendaXml implements Serializable{

    private List<Contacto> agendaMovil;

    public AgendaXml(Context c){
        agendaMovil=getListaContactos(c);
    }

    public List getListaNum(Context c, long id){
        return getListaTelefonos(c,id);
    }

    public void setAgenda(List aux){
        agendaMovil=aux;
    }

    public int getNewId(){return agendaMovil.size();}
    public List getListaCont(){return agendaMovil;}

    @Override
    //Devuelve una cadena con todos los datos de la agenda
    public String toString(){
        String s="";
        for(Object aux:agendaMovil){
            s+=aux.toString();
        }
        return s;
    }

    /****************************Metodos para obtener los contactos del movil**********************/
    public List<Contacto> getListaContactos(Context contexto){

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Contacto contacto;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();


        while(cursor.moveToNext()){
            contacto = new Contacto();
            contacto.setId((int) cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));
            lista.add(contacto);
        }
        return lista;
    }
    public List<String> getListaTelefonos(Context contexto, long id){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id+""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while(cursor.moveToNext()){
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
        }
        return lista;
    }

}

