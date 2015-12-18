package com.example.lap.appblog;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    protected ListView listViewEntradas;
    protected ArrayList <Entrada> listaEntradas;
    protected FloatingActionButton btnFAB;
    protected Spinner spinnerFiltros;
    protected SearchView viewBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaEntradas = new ArrayList <Entrada>();
        llenarListaConDatosDummies();
        // Create the adapter to convert the array to views
        AdapterListaEntradas adapter = new AdapterListaEntradas(this, listaEntradas);
        // Attach the adapter to a ListView
        listViewEntradas = (ListView) findViewById(R.id.listViewEntradas);
        listViewEntradas.setAdapter(adapter);
        listViewEntradas.setOnItemClickListener(this);

        btnFAB = (FloatingActionButton) findViewById(R.id.fab);
        btnFAB.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this,position+"",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ContenidoEntradaActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab) {
            Intent intent = new Intent(this, PublicarEntradaActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, position+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        ArrayList <String> listaFiltros =  new ArrayList <String> ();
        listaFiltros.add("Título");
        listaFiltros.add("Contenido");
        listaFiltros.add("Autor");
        ArrayAdapter <String> adapter = new ArrayAdapter <String> (this, R.layout.item_spinner_filtros, listaFiltros);
        spinnerFiltros = (Spinner) menu.findItem(R.id.spinnerFiltros).getActionView();
        spinnerFiltros.setOnItemSelectedListener(this);
        spinnerFiltros.setAdapter(adapter);

        viewBusqueda = (SearchView) menu.findItem(R.id.viewBusqueda).getActionView();
        viewBusqueda.setQueryHint(getResources().getString(R.string.hint_view_busquedas));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.viewBusqueda:
                Toast.makeText(this,"Busqueda", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void llenarListaConDatosDummies() {
        for(int i=0;i<20;i++) {
            listaEntradas.add(new Entrada((i+1),"Titulo de entrada "+(i+1),"Autor "+(i+1), "17-12-2015", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque in risus velit. Sed placerat viverra mi, vitae gravida sapien pretium in. Aliquam feugiat consequat rutrum. Mauris accumsan non tortor imperdiet euismod. Etiam rhoncus cursus lorem, vitae ultricies felis eleifend vitae. Etiam id diam eget lacus finibus fermentum non vel eros. Morbi rhoncus eget nunc nec laoreet. Pellentesque sed ligula convallis arcu dictum tincidunt. Cras hendrerit et tortor vel congue. "+(i+1)));
        }
    }
}