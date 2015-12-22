package com.example.lap.appblog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lap on 17/12/2015.
 */
public class AdapterListaEntradas extends ArrayAdapter <Entrada> {

    protected Context context;
    protected ArrayList <Entrada> listaEntradas;
    protected ArrayList <Entrada> listaEntradasAux; // Para los filtros
    static final int FILTRO_GENERAL = 0;
    static final int FILTRO_TITULO = 1;
    static final int FILTRO_CONTENIDO = 2;
    static final int FILTRO_AUTOR = 3;

    public AdapterListaEntradas (Context context, ArrayList <Entrada> listaEntradas) {
        // TODO Auto-generated constructor stub
        super(context, 0, new ArrayList<Entrada>());
        this.listaEntradas = listaEntradas;
        this.context = context;
        listaEntradasAux = new ArrayList <Entrada> ();
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
        Entrada entrada = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.elemento_lista_entradas, parent, false);
        }

        // Lookup view for data population
        TextView txtTitulo = (TextView) convertView.findViewById(R.id.txtTituloEntradaListado);
        TextView txtAutor= (TextView) convertView.findViewById(R.id.txtAutorEntradaListado);
        TextView txtFecha = (TextView) convertView.findViewById(R.id.txtFechaEntradaListado);
        TextView txtContenido = (TextView) convertView.findViewById(R.id.txtContenidoEntradaListado);

        txtTitulo.setText(entrada.getTitulo());
        txtAutor.setText(entrada.getAutor());

        SimpleDateFormat fechaFormatoSQL = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fechaFormatoApp = new SimpleDateFormat("dd-MM-yyyy");

        Date fecha = null;
        try {
            fecha = fechaFormatoSQL.parse(entrada.getFecha());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtFecha.setText(fechaFormatoApp.format(fecha));

        if(entrada.getContenido().length() > 70) {
            txtContenido.setText(entrada.getContenido().substring(0, 71) + "...");
        } else {
            txtContenido.setText(entrada.getContenido());
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public void filtroBusqueda(String cadena, int seleccionFiltro) {
        cadena = cadena.toLowerCase();
        listaEntradas.clear();
        if (cadena.length() == 0) {
            listaEntradas.addAll(listaEntradasAux);
        } else {
            switch(seleccionFiltro) {
                case FILTRO_GENERAL:
                    for (Entrada ent : listaEntradasAux) {
                        if (ent.getTitulo().toLowerCase().contains(cadena)
                            || ent.getAutor().toLowerCase().contains(cadena)
                            || ent.getContenido().toLowerCase().contains(cadena)) {
                            listaEntradas.add(ent);
                        }
                    }
                    break;
                case FILTRO_TITULO:
                    for (Entrada ent : listaEntradasAux) {
                        if (ent.getTitulo().toLowerCase().contains(cadena)) {
                            listaEntradas.add(ent);
                        }
                    }
                    break;
                case FILTRO_AUTOR:
                    for (Entrada ent : listaEntradasAux) {
                        if (ent.getAutor().toLowerCase().contains(cadena)) {
                            listaEntradas.add(ent);
                        }
                    }
                    break;
                case FILTRO_CONTENIDO:
                    for (Entrada ent : listaEntradasAux) {
                        if (ent.getContenido().toLowerCase().contains(cadena)) {
                            listaEntradas.add(ent);
                        }
                    }
                    break;
            }
        }
        notifyDataSetChanged();
    }
}