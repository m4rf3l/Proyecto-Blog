/*
    EjecucionPeticiones
    Clase (ejecutada en segundo plano) que ejecuta las peticiones mediante los métodos GET, PUT, POST, ETC.
    y envía la respuesta obtenida a los activities mediante el método onTaskCompleted
 */

package com.example.lap.appblog;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EjecucionPeticiones extends AsyncTask <Integer, Integer, String> {

    protected int tipoOperacion; // GET, PUT, POST, ETC
    protected String URLString;
    protected OnPostExecute listener;
    protected HttpURLConnection conexion;
    protected URL url;
    protected String respuesta; // Aqui se guarda la respuesta de la peticion
    protected JSONObject datos; // Si se requieren enviar datos en la peticion
    protected Context contexto;
    protected Conexion con;     // Para verificar la conexión del dispositivo

    // Constantes para identificar metodos
    static final int GET = 0;
    static final int POST = 1;

    public EjecucionPeticiones(int tipoOperacion, String URLString, OnPostExecute listener, Context contexto){
        this.tipoOperacion = tipoOperacion;
        this.URLString = URLString;
        this.listener = listener;
        this.contexto = contexto;
        this.con = new Conexion(contexto);
    }

    // Metodo para asignar datos al objeto si se requiere enviar datos en la peticion
    public void setDatos(JSONObject datos){
        this.datos = datos;
    }

    @Override
    protected String doInBackground(Integer... params) {
        if(con.isOnline()) {
            switch (tipoOperacion) {
                case GET:
                    try {
                        url = new URL(URLString);
                        conexion = (HttpURLConnection) url.openConnection();
                        conexion.setDoInput(true);
                        conexion.setRequestMethod("GET");
                        conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader bf = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();
                            while ((inputLine = bf.readLine()) != null) {
                                response.append(inputLine);
                            }
                            bf.close();
                            respuesta = response.toString();
                        } else {
                            respuesta = conexion.getResponseMessage();
                        }
                        conexion.disconnect();
                    } catch (Exception e) {
                        Log.e("EJECUCION_GET", e.toString(), e);
                    }
                    break;
                case POST:
                    try {
                        url = new URL(URLString);
                        conexion = (HttpURLConnection) url.openConnection();
                        conexion.setDoOutput(true);
                        conexion.setRequestMethod("POST");
                        conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        OutputStream os = conexion.getOutputStream();
                        os.write(datos.toString().getBytes("UTF-8"));
                        os.close();
                        if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
                            String output;
                            StringBuffer response = new StringBuffer();
                            while ((output = br.readLine()) != null) {
                                response.append(output);
                            }
                            br.close();
                            respuesta = response.toString();
                        }
                        conexion.disconnect();
                    } catch (Exception e) {
                        Log.e("EJECUCION_POST", e.toString(), e);
                    }
                    break;
            }
        }
        return respuesta;
    }

    @Override
    protected void onPostExecute(String respuesta) {
        listener.onTaskCompleted(respuesta);
    }
}