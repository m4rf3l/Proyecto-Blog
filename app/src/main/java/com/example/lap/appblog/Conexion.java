package com.example.lap.appblog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Conexion {

    protected Context contexto;

    public Conexion(Context contexto) {
        this.contexto = contexto;
    }

    // Método para revisar la conexion del dispositivo
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}