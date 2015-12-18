package com.example.lap.appblog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Lap on 17/12/2015.
 */
class AdapterListaEntradas extends ArrayAdapter<Entrada> {

    protected Context context;
    protected ArrayList <Entrada> listaEntradas;

    public AdapterListaEntradas (Context context, ArrayList <Entrada> listaEntradas) {
        // TODO Auto-generated constructor stub
        super(context, 0, new ArrayList<Entrada>());
        this.listaEntradas = listaEntradas;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listaEntradas.size();
    }

    @Override
    public Entrada getItem(int position) {
        // TODO Auto-generated method stub
        return listaEntradas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Entrada ent = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.elemento_lista_entradas, parent, false);
        }
        // Lookup view for data population
        TextView txtTitulo = (TextView) convertView.findViewById(R.id.txtTituloEntradaListado);
        TextView txtAutor= (TextView) convertView.findViewById(R.id.txtAutorEntradaListado);
        TextView txtFecha = (TextView) convertView.findViewById(R.id.txtFechaEntradaListado);
        TextView txtContenido = (TextView) convertView.findViewById(R.id.txtContenidoEntradaListado);

        txtTitulo.setText(ent.getTitulo());
        txtAutor.setText(ent.getAutor());
        txtFecha.setText(ent.getFecha());
        txtContenido.setText(ent.getContenido());

        // Return the completed view to render on screen
        return convertView;
    }
}