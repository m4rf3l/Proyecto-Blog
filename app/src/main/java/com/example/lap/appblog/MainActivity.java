package com.example.lap.appblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    protected ListView listViewEntradas;
    protected ArrayList <Entrada> listaEntradas;
    protected FloatingActionButton btnFAB;
    protected Spinner spinnerFiltros;
    protected SearchView viewBusqueda;
    protected ProgressDialog dialogoCarga;
    protected AdapterListaEntradas adapter;
    protected ProgressBar indicadorCarga;
    protected Conexion conexion;
    protected boolean conectado;
    protected EntradasDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaEntradas = new ArrayList <Entrada> ();

        // Create the adapter to convert the array to views
        adapter = new AdapterListaEntradas(this, listaEntradas);

        listViewEntradas = (ListView) findViewById(R.id.listViewEntradas);

        // Attach the adapter to a ListView
        listViewEntradas.setAdapter(adapter);
        listViewEntradas.setOnItemClickListener(this);

        btnFAB = (FloatingActionButton) findViewById(R.id.fab);
        btnFAB.setOnClickListener(this);

        conexion = new Conexion(this);
        indicadorCarga = (ProgressBar) findViewById(R.id.indicador_carga);
        dataSource = new EntradasDataSource(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(conexion.isOnline()) {
            conectado = true;
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_600)));
            EntradasDataSource.EliminarEntradasBD  eliminarEntradas = new EntradasDataSource.EliminarEntradasBD();
            eliminarEntradas.execute();
            ObtencionEntradas obtencionEntradas = new ObtencionEntradas();
            indicadorCarga.setVisibility(View.VISIBLE);
            obtencionEntradas.hacerPeticion();
        } else {
            conectado = false;
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            //dataSource.obtenerTodasLasEntradas(listaEntradas, adapter);
            EntradasDataSource.ConsultaBDEntrada  consultaEntrada = new EntradasDataSource.ConsultaBDEntrada(listaEntradas, adapter);
            consultaEntrada.execute();
        }
    }

    @Override
    public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ContenidoEntradaActivity.class);
        intent.putExtra("titulo", listaEntradas.get(position).getTitulo());
        intent.putExtra("autor", listaEntradas.get(position).getAutor());
        intent.putExtra("fecha", listaEntradas.get(position).getFecha());
        intent.putExtra("contenido", listaEntradas.get(position).getContenido());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab && conectado) {
            Intent intent = new Intent(this, PublicarEntradaActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.aviso_problema_conexion_fab), Toast.LENGTH_SHORT).show();
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
        listaFiltros.add("FILTROS");
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

    private class ObtencionEntradas implements OnPostExecute {

        JSONArray respJSON;
        Peticiones peticion;

        public ObtencionEntradas() {

        }

        private void hacerPeticion() {
            peticion = new Peticiones(MainActivity.this);
            peticion.getEntradas(this);
        }

        private void procesarResultado(String respuesta) {
            if(respuesta != null) {
                try {
                    respJSON = new JSONArray(respuesta);
                    if(respJSON.length() != 0) {
                        listaEntradas.clear();
                        for(int i=respJSON.length()-1; i >=0 ;i--) {
                            JSONObject objEntrada = respJSON.getJSONObject(i);
                            Entrada entrada = new Entrada(objEntrada.getString("id"), objEntrada.getString("titulo"), objEntrada.getString("autor"), objEntrada.getString("fecha"), objEntrada.getString("contenido"));
                            listaEntradas.add(entrada);
                            //dataSource.guardarEntrada(entrada.getTitulo(), entrada.getAutor(), entrada.getFecha(), entrada.getContenido());
                            EntradasDataSource.InsercionBDEntrada  guardarEntrada = new EntradasDataSource.InsercionBDEntrada(entrada.getTitulo(), entrada.getAutor(), entrada.getFecha(), entrada.getContenido());
                            guardarEntrada.execute();
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.aviso_error_cargar_entradas), Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.aviso_error_cargar_entradas), Toast.LENGTH_SHORT).show();
                }
                indicadorCarga.setVisibility(View.INVISIBLE);
            } else {
                indicadorCarga.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, getResources().getString(R.string.aviso_error_cargar_entradas), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCompleted(String respuesta) {
            procesarResultado(respuesta);
        }
    }
}