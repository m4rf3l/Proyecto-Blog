package com.example.lap.appblog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EntradasReaderDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "entradas.db";
    public static final int DATABASE_VERSION = 1;

    public EntradasReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear la tabla entradas
        db.execSQL(EntradasDataSource.CREATE_ENTRADAS_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}