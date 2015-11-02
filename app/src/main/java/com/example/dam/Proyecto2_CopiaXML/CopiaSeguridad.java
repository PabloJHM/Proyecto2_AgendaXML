package com.example.dam.Proyecto2_CopiaXML;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CopiaSeguridad extends AppCompatActivity{

    public void escribir(Context contexto, String archivo, List<Contacto> lista) throws IOException {
        FileOutputStream fosxml = new FileOutputStream(new File(contexto.getExternalFilesDir(null),archivo));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, Boolean.valueOf(true));
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        docxml.startTag(null, "contactos");
        for(Contacto c : lista){
            docxml.startTag(null, "contacto");
                docxml.startTag(null, "nombre");
                    docxml.attribute(null, "id", c.getId() + "");
                    docxml.text(c.getNombre() + "");
                docxml.endTag(null, "nombre");
            for(String telf:c.getArrayTlf()) {
                    docxml.startTag(null, "telefono");
                        docxml.text(telf);
                    docxml.endTag(null, "telefono");
                }
            docxml.endTag(null, "contacto");
        }
        docxml.endDocument();
        docxml.flush();
        fosxml.close();
    }

    public List<Contacto> leer(Context co,String nombre) throws IOException, XmlPullParserException {
        List<Contacto> lis=new ArrayList();
        Contacto c;
        ArrayList <String> telf= new ArrayList<>();
        String nom="";
        XmlPullParser lectorxml = Xml.newPullParser();
        lectorxml.setInput(new FileInputStream(new File(co.getExternalFilesDir(null),nombre)),"utf-8");
        int evento = lectorxml.getEventType(),atrib=0;
        while (evento != XmlPullParser.END_DOCUMENT){
            if(evento == XmlPullParser.START_TAG){
                String etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("contacto")==0)
                    telf=new ArrayList<>();

                if(etiqueta.compareTo("nombre")==0){
                    atrib = Integer.parseInt(lectorxml.getAttributeValue(null, "id"));
                    nom=lectorxml.nextText();

                } else if(etiqueta.compareTo("telefono")==0){
                    String texto = lectorxml.nextText();
                    telf.add(texto);
                }
            }
            if(evento==XmlPullParser.END_TAG){
                String etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("contacto")==0){
                    c = new Contacto(nom,atrib,telf);
                    lis.add(c);
                }
            }
            evento = lectorxml.next();
        }
        return lis;
    }
}
