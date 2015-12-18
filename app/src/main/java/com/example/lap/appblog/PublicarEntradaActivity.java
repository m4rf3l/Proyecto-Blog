package com.example.lap.appblog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Lap on 17/12/2015.
 */
public class PublicarEntradaActivity extends AppCompatActivity {

    protected EditText edtTitulo;
    protected EditText edtAutor;
    protected EditText edtContenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_entrada);

        edtTitulo = (EditText) findViewById(R.id.edtTitulo);
        edtAutor = (EditText) findViewById(R.id.edtAutor);
        edtContenido = (EditText) findViewById(R.id.edtContenido);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_publicar_entrada, menu);
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
            case R.id.btnPublicar:
                if(validarCajas()) {
                    Toast.makeText(this, "CAMPOS COMPLETOS", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean validarCajas() {
        boolean camposCompletos = true;
        if(TextUtils.isEmpty(edtTitulo.getText().toString())) {
            Toast.makeText(this, getResources().getText(R.string.aviso_titulo_vacio), Toast.LENGTH_SHORT).show();
            camposCompletos = false;
        } else if (TextUtils.isEmpty(edtAutor.getText().toString())) {
            Toast.makeText(this, getResources().getText(R.string.aviso_autor_vacio), Toast.LENGTH_SHORT).show();
            camposCompletos = false;
        } else if (TextUtils.isEmpty(edtContenido.getText().toString())) {
            Toast.makeText(this, getResources().getText(R.string.aviso_contenido_vacio), Toast.LENGTH_SHORT).show();
            camposCompletos = false;
        }
        return camposCompletos;
    }
}
