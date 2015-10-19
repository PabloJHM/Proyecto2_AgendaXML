package com.example.dam.proyecto1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ClaseAdaptador extends ArrayAdapter<Contacto> {
    private Context ctx;
    private int res;
    private List<Contacto> agenda;
    private LayoutInflater lInflator;

    public ClaseAdaptador(Context context, int resource, List<Contacto> agenda) {
        super(context, resource, agenda);
        this.ctx=context;
        this.res=resource;
        this.agenda=agenda;
        lInflator=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder{
        public TextView tvNom,tvTlf;
        public ImageView ivP,ivM,ivPer,ivAdd;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Inflador
        ViewHolder vh=new ViewHolder();
        if(convertView==null){
            convertView = lInflator.inflate(res,null);
            TextView tv = (TextView) convertView.findViewById(R.id.tvNombre);
            vh.tvNom = tv;
            tv = (TextView) convertView.findViewById(R.id.tvTlf);
            vh.tvTlf = tv;
            ImageView iv =(ImageView) convertView.findViewById(R.id.ivPerso);
            vh.ivPer=iv;
            iv =(ImageView) convertView.findViewById(R.id.ivPlus);
            vh.ivP=iv;
            iv=(ImageView) convertView.findViewById(R.id.ivMinus);
            vh.ivM=iv;
            iv=(ImageView) convertView.findViewById(R.id.ivAdd);
            vh.ivAdd=iv;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }

        //Asignacion de datos
        vh.tvNom.setText(agenda.get(position).getNombre());
        vh.tvTlf.setText(agenda.get(position).getFirstTlf());
        vh.ivP.setId(position);
        vh.ivM.setId(position);

        //Comprobamos cuantos numeros tiene un contacto, si tiene más de 1 aparecerá el boton +
        // de lo contrario aparecerá el botton -
        if(agenda.get(position).getSize()>1){
            vh.ivM.setVisibility(View.GONE);
            vh.ivP.setVisibility(View.VISIBLE);
        } else {
            vh.ivM.setVisibility(View.VISIBLE);
            vh.ivP.setVisibility(View.GONE);
        }
        return convertView;
    }

    //Mensaje con los numeros de tlf del contacto tras pulsar +
    public void mostrar(View v){
        Contacto aux=agenda.get(v.getId());

        String s=ctx.getString(R.string.mExtraTlf)+aux.getNombre()+ctx.getString(R.string.salto);
        s+=aux.getTlf();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(s);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
