package com.example.lap.appblog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Lap on 17/12/2015.
 */
public class ContenidoEntradaActivity extends AppCompatActivity {

    protected TextView txtTituloEntrada;
    protected TextView txtAutorEntrada;
    protected TextView txtFechaEntrada;
    protected TextView txtContenidoEntrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenido_entrada);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtTituloEntrada = (TextView) findViewById(R.id.txtTituloEntrada);
        txtAutorEntrada = (TextView) findViewById(R.id.txtAutorEntrada);
        txtFechaEntrada = (TextView) findViewById(R.id.txtFechaEntrada);
        txtContenidoEntrada = (TextView) findViewById(R.id.txtContenidoEntrada);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            txtTituloEntrada.setText(extras.getString("titulo"));
            txtAutorEntrada.setText(extras.getString("autor"));
            txtFechaEntrada.setText(extras.getString("fecha"));
            txtContenidoEntrada.setText(extras.getString("contenido"));
        } else {
            Toast.makeText(this, "Error al mostrar el contenido de la entrada.", Toast.LENGTH_SHORT).show();
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
