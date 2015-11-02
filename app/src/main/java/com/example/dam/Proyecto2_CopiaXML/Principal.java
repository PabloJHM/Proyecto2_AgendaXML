package com.example.dam.Proyecto2_CopiaXML;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Principal extends AppCompatActivity{

    private ListView lv;
    private ClaseAdaptador cl;
    private AgendaXml am;
    private CopiaSeguridad cs;
    private List<Contacto> agenda;
    private boolean cAg,cXml,cSync;
    private static final int ANIADIR=0,EDITAR=1;
    private final String nomArchivo = "copiaSeguridad.xml", nomArchivoTotal="copiaTotal.xml";
    private File archivoInc,archivoTotal;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Intent i= this.getIntent();
        cAg=i.getBooleanExtra("agendakey", false);
        cXml=i.getBooleanExtra("xmlkey", false);
        cSync=i.getBooleanExtra("synkey",false);
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Menú principal con la opcion para añadir un nuevo contacto
        if (id == R.id.mnAñadir) {
            añadir();
            return true;
        }
        if (id == R.id.mnOrdenar1){
            Collections.sort(agenda);
            cl.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.mnOrdenar2){
            Collections.sort(agenda,Collections.reverseOrder());
            cl.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.mnleertotal){
            try {
                cargaTotal();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            cl.notifyDataSetChanged();
            return true;
        }
        if(id==R.id.saveInc){
            try {
                saveInc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(id==R.id.saveTotal){
            try {
                saveTotal();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }
        /**************************************Clase de inicio*************************************/
    public void init() throws IOException, XmlPullParserException {
        lv=(ListView) findViewById(R.id.lvLista);
        archivoInc= new File(getExternalFilesDir(null),nomArchivo);
        archivoTotal= new File(getExternalFilesDir(null),nomArchivoTotal);
        cs=new CopiaSeguridad();
        am=new AgendaXml(this);
        agenda=new ArrayList<>();
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        if(prefs.getString("Check", "error").equals("true") || cSync){
            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("LastSync",hourdateFormat.format(date));
            ed.commit();
            agenda=synchro();
            am.setAgenda(agenda);
            creaAdap(agenda);
        } else if(cXml) {
            agenda = cs.leer(this, nomArchivo);
            creaAdap(agenda);
            am.setAgenda(agenda);
        } else if(cAg){
            agenda=am.getListaCont();
            creaAdap(agenda);
        }


        //Implementamos a cada contacto sus telefonos, guardados en un List
        for(Contacto aux:agenda){
            aux.setTlf(am.getListaNum(this, aux.getId()));
        }

    }

    /**************************************Adaptador*************************************/
    public void creaAdap(List<Contacto> aux) {
        cl = new ClaseAdaptador(this, R.layout.elemento_lista, aux);//declaro el adaptador
        lv.setAdapter(cl);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                llamar(posicion);
            }
        });
        registerForContextMenu(lv);
    }


    /****************************Menú contextual****************************/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);
    }

    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo vistainfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int posicion = vistainfo.position;
        switch(item.getItemId()){

            case R.id.mnBorrar:
                borrar(posicion);
                return true;

            case R.id.mnEditar:
                editar(posicion);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**************************Métodos del menú contextual******************************/
    public void añadir(){
        Intent i=new Intent (this, Anadir.class);
        startActivityForResult(i, ANIADIR);
    }

    public void borrar(final int posicion){
        //Lanzamos un Dialogo para la confirmación para borrar un contacto
        String s=this.getString(R.string.elminarq)+agenda.get(posicion).getNombre()+this.getString(R.string.qclose);
        AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
        mensaje.setTitle(R.string.menTitulo);
        mensaje.setMessage(s);
        mensaje.setCancelable(false);
        mensaje.setNegativeButton(this.getString(R.string.bCancelar), null);
        mensaje.setPositiveButton(this.getString(R.string.bAceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface mensaje, int id) {
                //Si acepta, eliminamos el contacto
                agenda.remove(posicion);
                cl.notifyDataSetChanged();
            }
        });
        mensaje.show();
    }

    public void editar(int posicion){
        Contacto aux=agenda.get(posicion);
        Intent i=new Intent (this, Editar.class);
        i.putExtra(R.string.kContacto + "", (Parcelable) aux); //Parcelable para poder pasar un objeto a través de un Intent
        startActivityForResult(i, EDITAR);
    }

    public int getPos(long id){
        for (int i=0;i<agenda.size();i++){
            if(agenda.get(i).getId()==(id))
                return i;
        }
        return -1;
    }

    public void edita(Contacto aux){
        Contacto edi=agenda.get(getPos(aux.getId()));
        edi.setNombre(aux.getNombre());
        edi.setTlf(aux.getArrayTlf());
    }

    /**************************Acciones de los bottones + y - ********************************/
    public void mostrar(View v){
        cl.mostrar(v);
    }

     public void minus(final View v) {
         //Al pulsar el - se abrirá un popup menú con la opción de agregar teléfonos, que nos
         // enviará a la edicioón del contacto donde podremos añadir nuevos telefonos
         PopupMenu popup = new PopupMenu(this, v);
         MenuInflater inflater = popup.getMenuInflater();
         inflater.inflate(R.menu.menu_minus, popup.getMenu());
         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 editar(v.getId());
                 return true;
             }
         });
         popup.show();
     }

    public void btAñadir(View v){
        añadir();
    }

    /*********************************Actualización y ordenación************************************/

    public void refrescar() {
        Collections.sort(agenda);
        cl.notifyDataSetChanged();
    }

    /**********************************Metodos de ayuda y de xml***********************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK && requestCode == EDITAR){
            Contacto aux= (Contacto) data.getSerializableExtra("aux");
            edita(aux);
            refrescar();
        }  else if(resultCode == Activity.RESULT_OK && requestCode == ANIADIR){
            Contacto aux= (Contacto) data.getSerializableExtra("aux");
            agenda.add(aux);
            refrescar();
        }
    }

    public void saveInc() throws IOException {
        cs.escribir(this, nomArchivo, agenda);
    }

    public void saveTotal() throws IOException {
        cs.escribir(this, nomArchivoTotal, agenda);
    }

    //Metodo que comprueba cuales son los telefonos repetidos y cuales no entre el archivo xml y
    //la agenda, y lo fusiona en un solo List para trabjar sobre él
    public List<Contacto> synchro() throws IOException, XmlPullParserException {
        List<Contacto> telefono=am.getListaCont(),incremental=cs.leer(this,nomArchivo);
        List <String> x,y;
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

    //Al seleccionar un contacto, llama a dicho contacto (Al numero principal)
    public void llamar(int pos){
        String s=agenda.get(pos).getSelectedTlf(0);
        Uri numero = Uri.parse( "tel:" + s.toString() );
        Intent i = new Intent(Intent.ACTION_CALL, numero);
        startActivity(i);
    }

    public void cargaTotal() throws IOException, XmlPullParserException {
        agenda=cs.leer(this,nomArchivoTotal);
        creaAdap(agenda);
        cl.notifyDataSetChanged();
    }
}
