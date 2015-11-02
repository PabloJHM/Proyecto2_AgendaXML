package com.example.dam.Proyecto2_CopiaXML;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo on 01/11/2015.
 */
public class Seleccion extends AppCompatActivity{
    private Button btAgenda,btXml,btSynch;
    private CheckBox cbSynch;
    private TextView tvSincro,tvFrase;
    private final String nomArchivo = "copiaSeguridad.xml", nomArchivoTotal="copiaTotal.xml";
    private File archivoInc,archivoTotal;
    private CopiaSeguridad cs;
    private ClaseAdaptador cl;
    private ListView lv;
    private List<Contacto> agenda;
    private AgendaXml am;
    SharedPreferences prefs;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccion);
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() throws IOException {
        cs= new CopiaSeguridad();
        am=new AgendaXml(this);
        agenda=new ArrayList<>();
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        btAgenda= (Button) findViewById(R.id.cargarAgenda);
        btXml= (Button) findViewById(R.id.cargarXml);
        btSynch= (Button) findViewById(R.id.synchro);
        cbSynch = (CheckBox) findViewById(R.id.cbSynchro);
        tvSincro=(TextView)findViewById(R.id.fechasync);
        tvFrase=(TextView)findViewById(R.id.frase);

        if(prefs.getString("Check", "error").equals("true")){
            cbSynch.setChecked(true);
        }

        if(!prefs.getString("LastSync", "error").equals("error")){
            tvFrase.setText(R.string.lastSync);
            tvSincro.setText(prefs.getString("LastSync", "error"));
        } else {
            tvFrase.setText("");
            tvSincro.setText(R.string.noSync);
        }

        archivoInc= new File(getExternalFilesDir(null),nomArchivo);
        archivoTotal= new File(getExternalFilesDir(null),nomArchivoTotal);

        if(!archivoInc.exists()){
            agenda=am.getListaCont();
            archivoInc = new File(getExternalFilesDir(null), nomArchivo);
            cs.escribir(this, nomArchivo, agenda);
        }
        if(!archivoTotal.exists()){
            agenda=am.getListaCont();
            archivoTotal= new File(getExternalFilesDir(null),nomArchivoTotal);
            cs.escribir(this, nomArchivoTotal, agenda);
        }
    }


    public void cargaAgenda(View v){
        Intent i=new Intent (this, Principal.class);
        i.putExtra("agendakey",true);
        startActivity(i);
    }

    public void cargaXml(View v){
        Intent i=new Intent (this, Principal.class);
        i.putExtra("xmlkey", true);
        startActivity(i);
    }

    public void cargaSincro(View v){
        Intent i=new Intent (this, Principal.class);
        i.putExtra("synkey", true);
        startActivity(i);
    }

    public void sincronizar(View v){
        if(cbSynch.isChecked()){
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("Check", "true");
            ed.commit();
        } else {
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("Check", "false");
            ed.commit();
        }
    }

    public List<Contacto> mezcla() throws IOException, XmlPullParserException {//metodo que mezcla mi xml incremental con los datos del telefono
        List<Contacto> telefono=am.getListaCont(),incremental=cs.leer(this,nomArchivo);
        List <String>x,y;
        List<Contacto> result=new ArrayList<>();
        result.addAll(telefono);

        for(int z=0;z<incremental.size();z++) {
            int cont1=0;
            for(int q=0;q<telefono.size();q++) {
                if(incremental.get(z).getNombre().equalsIgnoreCase(telefono.get(q).getNombre())){
                    x=telefono.get(q).getArrayTlf();
                    y=incremental.get(z).getArrayTlf();

                    for (int j = 0; j < y.size(); j++) {
                        int cont2 = 0;
                        for (int i = 0; i < x.size(); i++)
                            if (!x.get(i).equals(y.get(j)))
                                cont2++;

                        if (cont2 >=x.size())
                            x.add(y.get(j));
                    }

                }else
                    cont1++;
            }
            if(cont1>=telefono.size())
                result.add(incremental.get(z));
        }
        return result;
    }
}
