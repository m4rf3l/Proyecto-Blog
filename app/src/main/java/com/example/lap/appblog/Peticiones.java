package com.example.lap.appblog;

import android.content.Context;
import org.json.JSONObject;

public class Peticiones {

    protected String URLGuardarEntrada;
    protected String URLObtenerEntradas;
    protected EjecucionPeticiones peticion;
    protected Context contexto;

    // Constantes para identificar metodos
    static final int GET = 0;
    static final int POST = 1;

    public Peticiones(Context contexto){
        this.contexto = contexto;
    }

    public void getEntradas(OnPostExecute listener){
        URLObtenerEntradas = "http://app-blog.hol.es/blog/metodos/obtenerEntradas.php";
        peticion = new EjecucionPeticiones(GET, URLObtenerEntradas, listener, contexto);
        peticion.execute();
    }

    public void postEntrada(JSONObject datos, OnPostExecute listener){
        URLGuardarEntrada = "http://app-blog.hol.es/blog/metodos/guardarEntrada.php";
        peticion = new EjecucionPeticiones(POST, URLGuardarEntrada, listener, contexto);
        peticion.setDatos(datos);
        peticion.execute();
    }
}