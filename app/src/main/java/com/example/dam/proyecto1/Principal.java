package com.example.dam.proyecto1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity{

    private ListView lv;
    private static ClaseAdaptador cl;
    private List<Contacto> agenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        init();
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
            AgendaMovil.ordenaDesc();
            cl.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.mnOrdenar2){
            AgendaMovil.ordenar();
            cl.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
        /**************************************Clase de inicio*************************************/
    public void init(){
        lv=(ListView) findViewById(R.id.lvLista);
        AgendaMovil a =new AgendaMovil(this);

        //Rellenamos el listView con los contactos del teléfono usando el método ubicado en AgendaMovil
        agenda=a.getListaCont();

        //Implementamos a cada contacto sus telefonos, guardados en un List
        for(Contacto aux:agenda){
            aux.setTlf(a.getListaNum(this, aux.getId()));
        }

        //Lanzamos el adaptador
        cl = new ClaseAdaptador(this, R.layout.elemento_lista, agenda);
        lv.setAdapter(cl);

        registerForContextMenu(lv);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);
    }

    /****************************Menú contextual****************************/

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
        startActivity(i);
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
        i.putExtra(R.string.kContacto+"", (Parcelable) aux); //Parcelable para poder pasar un objeto a través de un Intent
        startActivity(i);
    }

    /**************************Acciones de los bottones + y -********************************/
    public void mostrar(View v){
        cl.mostrar(v);
    }

     public void minus(final View v) {
         //Al pulsar el - se abrirá un popup menú con la opción de agregar teléfonos, que nos
         // enviará a la edicioón del contacto donde podremos añadir nuevos telefonos
         PopupMenu popup = new PopupMenu(this, v);
         MenuInflater inflater = popup.getMenuInflater();
         inflater.inflate(R.menu.menu_minus, popup.getMenu());
         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
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

    // Metodo para ordenar el listview. Lo llamo cada vez que añado o edito un contacto
    public static void refrescar(){
        AgendaMovil.ordenar();
        cl.notifyDataSetChanged();
    }
}
