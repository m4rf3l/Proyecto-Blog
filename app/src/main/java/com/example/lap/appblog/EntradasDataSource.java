package com.example.lap.appblog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Adapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Lap on 20/12/2015.
 */
public class EntradasDataSource {

    //Metainformación de la base de datos
    public static final String NOMBRE_TABLA_ENTRADAS = "entradas";
    public static final String STRING_TYPE = "text";
    public static final String INT_TYPE = "integer";
    public static final String DATE_TYPE = "date";


    //Campos de la tabla entradas
    public static class ColumnasEntrada {
        public static final String ID_ENTRADA = "id";
        public static final String TITULO_ENTRADA = "titulo";
        public static final String AUTOR_ENTRADA = "autor";
        public static final String FECHA_ENTRADA = "fecha";
        public static final String CONTENIDO_ENTRADA = "contenido";
    }

    //Script de creación de la tabla entradas
    public static final String CREATE_ENTRADAS_SCRIPT =
            "create table "+NOMBRE_TABLA_ENTRADAS+"(" +
                    ColumnasEntrada.ID_ENTRADA+" "+INT_TYPE+" primary key autoincrement," +
                    ColumnasEntrada.TITULO_ENTRADA+" "+STRING_TYPE+" not null," +
                    ColumnasEntrada.AUTOR_ENTRADA+" "+STRING_TYPE+" not null," +
                    ColumnasEntrada.FECHA_ENTRADA+" "+DATE_TYPE+" not null," +
                    ColumnasEntrada.CONTENIDO_ENTRADA+" "+STRING_TYPE+" not null)";

    private EntradasReaderDbHelper openHelper;
    private static SQLiteDatabase database;

    public EntradasDataSource(Context context) {
        //Creando una instancia hacia la base de datos
        openHelper = new EntradasReaderDbHelper(context);
        database = openHelper.getWritableDatabase();
    }

    /*
    public void guardarEntrada(String titulo, String autor, String fecha, String contenido) {
        //Nuestro contenedor de valores
        ContentValues values = new ContentValues();
        values.put(ColumnasEntrada.TITULO_ENTRADA, titulo);
        values.put(ColumnasEntrada.AUTOR_ENTRADA, autor);
        values.put(ColumnasEntrada.FECHA_ENTRADA, fecha);
        values.put(ColumnasEntrada.CONTENIDO_ENTRADA, contenido);
        //Insertando en la base de datos
        database.insert(NOMBRE_TABLA_ENTRADAS, null, values);
    }
    */

    /*
    public void obtenerTodasLasEntradas(ArrayList <Entrada> listaEntradas, AdapterListaEntradas adapter) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + NOMBRE_TABLA_ENTRADAS, null);
        listaEntradas.clear();
        while(cursor.moveToNext()) {
            // 1 = titulo; 2 = autor; 3 = fecha; 4 = contenido
            Entrada entrada = new Entrada(cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(4));
            listaEntradas.add(entrada);
            adapter.notifyDataSetChanged();
        }
    }
    */

    /*
    public void eliminarTodasLasEntradas() {
        database.delete("entradas", null, null);
    }
    */

    public static class InsercionBDEntrada extends AsyncTask <Integer, Integer, String> {

        String titulo;
        String autor;
        String fecha;
        String contenido;

        public InsercionBDEntrada(String titulo, String autor, String fecha, String contenido) {
            this.titulo = titulo;
            this.autor = autor;
            this.fecha = fecha;
            this.contenido = contenido;
        }

        @Override
        protected String doInBackground(Integer... params) {
            //Nuestro contenedor de valores
            ContentValues values = new ContentValues();
            values.put(ColumnasEntrada.TITULO_ENTRADA, titulo);
            values.put(ColumnasEntrada.AUTOR_ENTRADA, autor);
            values.put(ColumnasEntrada.FECHA_ENTRADA, fecha);
            values.put(ColumnasEntrada.CONTENIDO_ENTRADA, contenido);
            //Insertando en la base de datos
            database.insert(NOMBRE_TABLA_ENTRADAS, null, values);
            return null;
        }
    }

    public static class ConsultaBDEntrada extends AsyncTask <Integer, Integer, String> {

        ArrayList <Entrada> listaEntradas;
        AdapterListaEntradas adapter;

        public ConsultaBDEntrada(ArrayList <Entrada> listaEntradas, AdapterListaEntradas adapter) {
            this.listaEntradas = listaEntradas;
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(Integer... params) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + NOMBRE_TABLA_ENTRADAS, null);
            listaEntradas.clear();
            while(cursor.moveToNext()) {
                // 1 = titulo; 2 = autor; 3 = fecha; 4 = contenido
                Entrada entrada = new Entrada(cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(4));
                listaEntradas.add(entrada);

            }
            return null;
        }

        @Override
        protected void onPostExecute(String respuesta) {
            adapter.notifyDataSetChanged();
        }
    }

    public static class EliminarEntradasBD extends AsyncTask <Integer, Integer, String> {

        public EliminarEntradasBD() {

        }

        @Override
        protected String doInBackground(Integer... params) {
            database.delete("entradas", null, null);
            return null;
        }

        @Override
        protected void onPostExecute(String respuesta) {

        }
    }
}