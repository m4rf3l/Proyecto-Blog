package com.example.lap.appblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    protected ListView listViewEntradas;
    protected ArrayList <Entrada> listaEntradas;
    protected FloatingActionButton btnFAB;
    protected Spinner spinnerFiltros;
    protected SearchView viewBusqueda;
    protected ProgressDialog dialogoCarga;
    protected AdapterListaEntradas adapter;
    protected ProgressBar indicadorCarga;
    protected Conexion conexion;
    protected EntradasDataSource dataSource;
    protected boolean conectado;
    protected boolean buscando;
    protected int filtroSeleccion;
    protected SwipeRefreshLayout swipeLayout;

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
        listViewEntradas.setEmptyView(findViewById(R.id.txtListViewVacio));

        btnFAB = (FloatingActionButton) findViewById(R.id.fab);
        btnFAB.setOnClickListener(this);

        conexion = new Conexion(this);
        indicadorCarga = (ProgressBar) findViewById(R.id.indicador_carga);
        dataSource = new EntradasDataSource(this);

        buscando = false;
        filtroSeleccion = 0;

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        EntradasDataSource.EliminarEntradasBD eliminarEntradas = new EntradasDataSource.EliminarEntradasBD();
        eliminarEntradas.execute();
        ObtencionEntradas obtencionEntradas = new ObtencionEntradas();
        dialogoCarga = ProgressDialog.show(this,"" , getResources().getString(R.string.contenido_dialog_cargando_entradas), false);
        obtencionEntradas.hacerPeticion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (conexion.isOnline()) {
            conectado = true;
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_600)));
            /*
            EntradasDataSource.EliminarEntradasBD eliminarEntradas = new EntradasDataSource.EliminarEntradasBD();
            if(! buscando) {
                eliminarEntradas.execute();
                ObtencionEntradas obtencionEntradas = new ObtencionEntradas();
                dialogoCarga = ProgressDialog.show(this,"" , getResources().getString(R.string.contenido_dialog_cargando_entradas), false);
                obtencionEntradas.hacerPeticion();
            }
            */
        } else {
            conectado = false;
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            if(! buscando) {
                EntradasDataSource.ConsultaBDEntrada consultaEntrada = new EntradasDataSource.ConsultaBDEntrada(listaEntradas, adapter, adapter.listaEntradasAux);
                consultaEntrada.execute();
            }
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
        filtroSeleccion = position;
        onQueryTextChange(viewBusqueda.getQuery().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        ArrayList <String> listaFiltros =  new ArrayList <String> ();
        listaFiltros.add(getResources().getString(R.string.filtro_item_spinner));
        listaFiltros.add(getResources().getString(R.string.titulo_item_spinner));
        listaFiltros.add(getResources().getString(R.string.contenido_item_spinner));
        listaFiltros.add(getResources().getString(R.string.autor_item_spinner));
        ArrayAdapter <String> adapter = new ArrayAdapter <String> (this, R.layout.item_spinner_filtros, listaFiltros);
        spinnerFiltros = (Spinner) menu.findItem(R.id.spinnerFiltros).getActionView();
        spinnerFiltros.setOnItemSelectedListener(this);
        spinnerFiltros.setAdapter(adapter);

        viewBusqueda = (SearchView) menu.findItem(R.id.viewBusqueda).getActionView();
        viewBusqueda.setQueryHint(getResources().getString(R.string.hint_view_busquedas));
        viewBusqueda.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String cadena) {
        if(cadena.length() == 0) {
            buscando = false;
        } else {
            buscando = true;
        }
        adapter.filtroBusqueda(cadena, filtroSeleccion);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000);
        if (conexion.isOnline()) {
            EntradasDataSource.EliminarEntradasBD eliminarEntradas = new EntradasDataSource.EliminarEntradasBD();
            eliminarEntradas.execute();
            ObtencionEntradas obtencionEntradas = new ObtencionEntradas();
            //dialogoCarga = ProgressDialog.show(this,"" , getResources().getString(R.string.contenido_dialog_cargando_entradas), false);
            obtencionEntradas.hacerPeticion();
        }
    }

private class ObtencionEntradas implements OnPostExecute {

        JSONArray respJSON;
        Peticiones peticion;

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
                        adapter.listaEntradasAux.clear();
                        for(int i=respJSON.length()-1; i >=0 ;i--) {
                            JSONObject objEntrada = respJSON.getJSONObject(i);
                            Entrada entrada = new Entrada(objEntrada.getString("id"), objEntrada.getString("titulo"), objEntrada.getString("autor"), objEntrada.getString("fecha"), objEntrada.getString("contenido"));
                            listaEntradas.add(entrada);
                            adapter.listaEntradasAux.add(entrada);
                            EntradasDataSource.InsercionBDEntrada  guardarEntrada = new EntradasDataSource.InsercionBDEntrada(entrada.getTitulo(), entrada.getAutor(), entrada.getFecha(), entrada.getContenido());
                            guardarEntrada.execute();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.aviso_error_cargar_entradas), Toast.LENGTH_SHORT).show();
                }
                dialogoCarga.dismiss();
            } else {
                dialogoCarga.dismiss();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.aviso_error_cargar_entradas), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCompleted(String respuesta) {
            procesarResultado(respuesta);
        }
    }
}