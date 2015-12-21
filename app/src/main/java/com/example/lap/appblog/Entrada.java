package com.example.lap.appblog;

/**
 * Created by Lap on 17/12/2015.
 */
public class Entrada {

    protected String id;
    protected String titulo;
    protected String autor;
    protected String fecha;
    protected String contenido;

    public Entrada(String id, String titulo, String autor, String fecha, String contenido) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.fecha = fecha;
        this.contenido = contenido;
    }

    public Entrada(String titulo, String autor, String fecha, String contenido) {
        this.titulo = titulo;
        this.autor = autor;
        this.fecha = fecha;
        this.contenido = contenido;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getFecha() {
        return fecha;
    }

    public String getContenido() {
        return contenido;
    }
}
