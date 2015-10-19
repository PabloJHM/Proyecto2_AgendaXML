package com.example.dam.proyecto1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Anadir extends Activity {

    long id;
    String nombre,tlf,tlf2,tlf3;
    EditText etANombre,etATlf,etAExtraTlf,etAExtraTlf2;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir);

        //Declaracion de los elementos
        etANombre=(EditText)findViewById(R.id.etANombre);
        etATlf=(EditText)findViewById(R.id.etATlf);
        etAExtraTlf=(EditText)findViewById(R.id.etAExtraTlf);
        etAExtraTlf2=(EditText)findViewById(R.id.etAExtraTlf2);

        //Asignamos al id el tamaño de la agenda, que siempre será 1 más que el ultimo id de la lista
        id=AgendaMovil.getNewId();
    }

    public void guardar(View v){
         List<String> listaTlf=new ArrayList<>();

         //Recogemos los datos del formulario
         nombre= etANombre.getText().toString();
         tlf= etATlf.getText().toString();
         tlf2= etAExtraTlf.getText().toString();
         tlf3= etAExtraTlf2.getText().toString();

        //Comprobamos que no ha dejado los campos del nombre o telefono vacios y añadimos los telefonos
        // a nuestro List auxiliar
        if(!nombre.isEmpty() && !tlf.isEmpty()){
            listaTlf.add(tlf);
            if(tlf2.length()>0){
                String secTlf=etAExtraTlf.getText().toString();
                listaTlf.add(secTlf);
            } if(tlf3.length()>0){
                String terTlf=etAExtraTlf2.getText().toString();
                listaTlf.add(terTlf);
            }
            //Añadimos el nuevo contacto a la agenda
            AgendaMovil.setContacto(new Contacto( nombre,id,listaTlf));

            //Actualizamos y ordenamos la agenda
            Principal.refrescar();

            //Mensaje de que fué añadido con éxito
            Toast.makeText(this, R.string.guardado,
                    Toast.LENGTH_SHORT).show();

            //Volvemos a la actividad principal
            finish();
        }else {
            Toast.makeText(this, R.string.vacio,
                    Toast.LENGTH_SHORT).show();
        }
    }

}
