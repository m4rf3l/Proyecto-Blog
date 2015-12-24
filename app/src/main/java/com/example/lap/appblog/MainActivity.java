/*
    MainActivity
    Activity principal de la App; muestra el listado de todas las entradas
    y vistas para las siguientes operaciones: Busqueda/Filtrado de entradas,
    publicación de entradas y acceso a la información de cada entrada.
 */

package com.example.lap.appblog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    protected ListView listViewEntradas;
    protected ArrayList <Entrada> listaEntradas;
    // Botón para iniciar PublicarEntradaActivity
    protected FloatingActionButton btnFAB;
    protected Spinner spinnerFiltros;
    protected SearchView viewBusqueda;
    protected ProgressDialog dialogoCarga;
    protected AdapterListaEntradas adapter;
    protected Conexion conexion;
    protected EntradasDataSource dataSource;
    protected SwipeRefreshLayout swipeLayout;
    // Bandera para el control de las operaciones cuando se está realizando una busqueda/filtrado
    protected boolean buscando;
    // Bandera para mostrar los dialogos de carga según haya recien creado la App o no.
    protected boolean onCreate;
    protected int filtroSeleccion;
    static final int PETICION_PUBLICACION_ENTRADAS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaEntradas = new ArrayList <Entrada> ();
        // Create the adapter to convert the array to views
        adapter = new AdapterListaEntradas(this, listaEntradas);
        listViewEntradas = (ListView) findViewById(R.id.listViewEntradas);
        listViewEntradas.setAdapter(adapter);
        listViewEntradas.setOnItemClickListener(this);
        listViewEntradas.setEmptyView(findViewById(R.id.txtListViewVacio));

        btnFAB = (FloatingActionButton) findViewById(R.id.fab);
        btnFAB.setOnClickListener(this);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.green_600));

        conexion = new Conexion(this);
        dataSource = new EntradasDataSource(this);

        buscando = false;
        onCreate = true;
        filtroSeleccion = 0;

        obtenerEntradas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
            Durante este estado de la App se verifica si el dispositivo
            cuenta con conexión, según sea el caso se actualiza la disponibilidad del
            botón para publicar nuevas entradas.
            Si no tiene conexión se obtienen las entradas guardadas en la BD para el "Modo Desconectado"
        */
        if (conexion.isOnline()) {
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_600)));
        } else {
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            if(! buscando) {
                EntradasDataSource.ConsultaBDEntrada consultaEntrada = new EntradasDataSource.ConsultaBDEntrada(listaEntradas, adapter, adapter.listaEntradasAux);
                consultaEntrada.execute();
            }
        }
    }

    @Override
    //Se ejecuta cuando se selecciona un Item del ListView
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
        if(v.getId() == R.id.fab && conexion.isOnline()) {
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_600)));
            Intent intent = new Intent(this, PublicarEntradaActivity.class);
            // Iniciar PublicarEntradaActivity
            startActivityForResult(intent, PETICION_PUBLICACION_ENTRADAS);
        } else {
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            Toast.makeText(this, getResources().getString(R.string.aviso_problema_conexion_fab), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    //Se ejecuta cuando se cambia el item seleccionado en el Spinner de Filtrado de Busquedas
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

        // ArrayList para llenar el Spinner de Filtrado de Busquedas
        ArrayList <String> listaFiltros =  new ArrayList <String> ();
        listaFiltros.add(getResources().getString(R.string.filtro_item_spinner));
        listaFiltros.add(getResources().getString(R.string.titulo_item_spinner));
        listaFiltros.add(getResources().getString(R.string.contenido_item_spinner));
        listaFiltros.add(getResources().getString(R.string.autor_item_spinner));
        ArrayAdapter <String> adapter = new ArrayAdapter <String> (this, R.layout.item_spinner_filtros, listaFiltros);
        spinnerFiltros = (Spinner) menu.findItem(R.id.spinnerFiltros).getActionView();
        spinnerFiltros.setOnItemSelectedListener(this);
        spinnerFiltros.setAdapter(adapter);
        spinnerFiltros.setBackgroundResource(R.drawable.selector_item_spinner);;

        // View para las busquedas
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
    // Se ejecuta cuando se teclean caracteres en la caja de busqueda
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
    // Se ejecuta cuando se actualiza el listado con "SwipeRefreshLayout"
    public void onRefresh() {
       obtenerEntradas();
    }

    public void obtenerEntradas() {
        if(onCreate) {
            dialogoCarga = ProgressDialog.show(this, "", getResources().getString(R.string.contenido_dialog_cargando_entradas), false);
            onCreate = false;
        } else {
            swipeLayout.setRefreshing(true);
        }
        if (conexion.isOnline()) {
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_600)));
            EntradasDataSource.EliminarEntradasBD eliminarEntradas = new EntradasDataSource.EliminarEntradasBD();
            eliminarEntradas.execute();
            ObtencionEntradas obtencionEntradas = new ObtencionEntradas();
            obtencionEntradas.hacerPeticion();
        } else {
            btnFAB.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            cerrarLoading();
            Toast.makeText(this, getResources().getString(R.string.aviso_problema_conexion_swipe), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dialogoCarga != null) {
            if (dialogoCarga.isShowing()) {
                dialogoCarga.cancel();
            }
        }
    }

    // Método para ocultar los dialogos de carga del listado de entradas.
    public void cerrarLoading() {
        if(dialogoCarga != null) {
            if (dialogoCarga.isShowing()) {
                dialogoCarga.dismiss();
            }
        }
        swipeLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PETICION_PUBLICACION_ENTRADAS) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null) {
                    // Se recibe una bandera de PublicarEntradaActivity, sí se publicaron nuevas entradas,
                    // entonces se actualiza el contenido del ListView para mostrar las nuevas entradas.
                    boolean publicacionRealizada = data.getBooleanExtra("publicacionRealizada", false);
                    if(publicacionRealizada) {
                        obtenerEntradas();
                    }
                }
            }
        }
    }

    /*
        ObtencionEntradas
        Clase que hace la petición al consumo de servicios para obtener
        las entradas almacenadas en el servidor,recibe el resultado
        enviado y lo procesa de acuerdo al mismo.
     */

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
                    listaEntradas.clear();
                    adapter.listaEntradasAux.clear();
                    if(respJSON.length() != 0) {
                        for(int i=0; i < respJSON.length(); i++) {
                            JSONObject objEntrada = respJSON.getJSONObject(i);
                            Entrada entrada = new Entrada(objEntrada.getString("titulo"), objEntrada.getString("autor"), objEntrada.getString("fecha"), objEntrada.getString("contenido"));
                            // Las entradas se almacenan en un ArrayList para el procesamiento del ListView
                            listaEntradas.add(entrada);
                            // Las entradas se almacenan en una BD para el "Modo Desconectado"
                            EntradasDataSource.InsercionBDEntrada guardarEntrada = new EntradasDataSource.InsercionBDEntrada(entrada.getTitulo(), entrada.getAutor(), entrada.getFecha(), entrada.getContenido());
                            guardarEntrada.execute();
                        }
                    }
                    // Se notifica al adapter para que actualice el ListView
                    adapter.notifyDataSetChanged();
                    // Las entradas se almacenan en un ArrayList Auxiliar para las Busquedas/Filtrado de entradas
                    adapter.listaEntradasAux.addAll(listaEntradas);
                }catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.aviso_error_cargar_entradas), Toast.LENGTH_SHORT).show();
                }
                cerrarLoading();
            } else {
                cerrarLoading();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.aviso_error_cargar_entradas), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCompleted(String respuesta) {
            procesarResultado(respuesta);
        }
    }
}