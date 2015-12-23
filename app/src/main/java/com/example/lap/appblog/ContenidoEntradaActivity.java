/*
    ContenidoEntradaActivity
    Activity que muestra la información (Título, Autor, Fecha y Contenido)
    de la entrada seleccionada en el ListView.
 */

package com.example.lap.appblog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContenidoEntradaActivity extends AppCompatActivity {

    protected TextView txtTituloEntrada;
    protected TextView txtAutorEntrada;
    protected TextView txtFechaEntrada;
    protected TextView txtContenidoEntrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenido_entrada);

        // Mostrar el botón "back" en el ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtTituloEntrada = (TextView) findViewById(R.id.txtTituloEntrada);
        txtTituloEntrada.setTypeface(txtTituloEntrada.getTypeface(), Typeface.BOLD);
        txtAutorEntrada = (TextView) findViewById(R.id.txtAutorEntrada);
        txtFechaEntrada = (TextView) findViewById(R.id.txtFechaEntrada);
        txtContenidoEntrada = (TextView) findViewById(R.id.txtContenidoEntrada);

        llenarCampos();
    }

    public void llenarCampos() {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            txtTituloEntrada.setText(extras.getString("titulo"));
            txtAutorEntrada.setText(extras.getString("autor"));
            // Formatear la fecha para mostrarla en formato dd-MM-yyyy
            SimpleDateFormat fechaFormatoSQL = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat fechaFormatoApp = new SimpleDateFormat("dd-MM-yyyy");
            Date fecha = null;
            try {
                fecha = fechaFormatoSQL.parse(extras.getString("fecha"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            txtFechaEntrada.setText(fechaFormatoApp.format(fecha));
            txtContenidoEntrada.setText(extras.getString("contenido"));
        } else {
            Toast.makeText(this, getResources().getString(R.string.aviso_error_mostrar_contenido), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_contenido_entrada, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
