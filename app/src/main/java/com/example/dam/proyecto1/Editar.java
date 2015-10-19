package com.example.dam.proyecto1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Editar extends Activity {

    Contacto aux=new Contacto();
    String nom,tlf;
    Bundle b;
    EditText etNom,etTlf,etEx1,etEx2;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        //Recibo los datos del contacto a editar
        Intent i= this.getIntent();
        b = i.getExtras();

        //Declaracion de los elementos
        aux=i.getParcelableExtra(R.string.kContacto+"");
        etNom=(EditText)findViewById(R.id.etENombre);
        etTlf=(EditText)findViewById(R.id.etETlf);
        etEx1=(EditText)findViewById(R.id.etEx1);
        etEx2=(EditText)findViewById(R.id.etEx2);

        //Asignamos valores a los elementos
        etNom.setText(aux.getNombre());
        etTlf.setText(aux.getFirstTlf());

        //Comprobamos si el contacto tiene más de un numero, si es asi se mostrarán en sus editText correspondientes
        if(aux.getSize()>1)
            etEx1.setText(aux.getSelectedTlf(1));
        if(aux.getSize()>2)
            etEx2.setText(aux.getSelectedTlf(2));
    }
     public void edGuardar(View v) {
         List<String> listaTlf=new ArrayList<>();

         //Recogemos los datos del formulario
         nom = etNom.getText().toString();
         tlf = etTlf.getText().toString();

         //Comprobamos que no ha dejado los campos del nombre o telefono vacios y añadimos los telefonos
         // a nuestro List auxiliar
         if(!nom.isEmpty() && !tlf.isEmpty()){
             listaTlf.add(tlf);
             if(etEx1.length()>0){
                 String secTlf=etEx1.getText().toString();
                 listaTlf.add(secTlf);
             } if(etEx2.length()>0){
                 String terTlf=etEx2.getText().toString();
                 listaTlf.add(terTlf);
             }
             //Actualizamos los datos del contacto
             aux.setNombre(nom);
             aux.setTlf(listaTlf);

             // Y lo guardamos
             AgendaMovil.guardarEditar(aux);

             // Actualizamos la agenda y la ordenamos, y mostramos un mensaje de que funcionó correctamente
             Principal.refrescar();
             Toast.makeText(this, R.string.editado,
                     Toast.LENGTH_SHORT).show();

             //volvemos a la actividad principal
             finish();
         }else{
             Toast.makeText(this, R.string.vacio,
                     Toast.LENGTH_SHORT).show();
         }
     }
}
