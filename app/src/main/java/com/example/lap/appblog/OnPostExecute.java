/*
    OnPostExecute
    Interface utilizada para notificar la finalizacion de las tareas en segundo plano.
 */

package com.example.lap.appblog;

public interface OnPostExecute {

    void onTaskCompleted(String respuesta);

}