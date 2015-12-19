package com.example.lap.appblog;

import android.content.Context;
import org.json.JSONObject;

public class Peticiones {

    protected String URLEntradas;
    protected EjecucionPeticiones peticion;
    protected Context contexto;

    // Constantes para identificar metodos
    static final int GET = 0;
    static final int POST = 1;

    public Peticiones(Context contexto){
        this.contexto = contexto;
    }

    public void postEntrada(JSONObject datos, OnPostExecute listener){
        URLEntradas = "http://10.0.3.2:7777/blog/metodos/guardarEntrada.php";
        peticion = new EjecucionPeticiones(POST, URLEntradas, listener, contexto);
        peticion.setDatos(datos);
        peticion.execute();
    }
}