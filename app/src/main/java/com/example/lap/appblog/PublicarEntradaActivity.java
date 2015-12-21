package com.example.lap.appblog;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Lap on 17/12/2015.
 */
public class PublicarEntradaActivity extends AppCompatActivity {

    protected EditText edtTitulo;
    protected EditText edtAutor;
    protected EditText edtContenido;
    protected ProgressDialog dialogoCarga;

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
                    String titulo = edtTitulo.getText().toString();
                    String autor = edtAutor.getText().toString();
                    String contenido = edtContenido.getText().toString();
                    Calendar calendario = Calendar.getInstance();
                    SimpleDateFormat fechaFormato = new SimpleDateFormat("yyyy-MM-dd"); //Formato MySQL
                    String fecha = fechaFormato.format(calendario.getTime());
                    Entrada entrada = new Entrada(titulo, autor, fecha, contenido);
                    PublicacionEntrada publicacion = new PublicacionEntrada(entrada);
                    dialogoCarga = ProgressDialog.show(this, getResources().getString(R.string.titulo_dialog_publicando_entrada), getResources().getString(R.string.contenido_dialog_publicando_entrada), false);
                    publicacion.hacerPeticion();
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

    public void limpiarCajas() {
        edtTitulo.setText("");
        edtAutor.setText("");
        edtContenido.setText("");
    }

    private class PublicacionEntrada implements OnPostExecute {

        Entrada entrada;

        public PublicacionEntrada(Entrada entrada) {
            this.entrada = entrada;
        }

        private void hacerPeticion(){
            guardarEntrada();
        }

        private void guardarEntrada() {
            Peticiones peticion =  new Peticiones(PublicarEntradaActivity.this);
            JSONObject datos = new JSONObject();
            try {
                datos.put("titulo", entrada.getTitulo());
                datos.put("autor", entrada.getAutor());
                datos.put("fecha", entrada.getFecha());
                datos.put("contenido", entrada.getContenido());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            peticion.postEntrada(datos, this);
        }

        private void procesarResultadoGuardarEntrada(String respuesta) {
            JSONObject respuestaJSON = null;
            if(respuesta != null) {
                    try {
                        respuestaJSON = new JSONObject(respuesta);
                        if (respuestaJSON.length() != 0) {
                            String mensaje = respuestaJSON.getString("msg");
                            Toast.makeText(PublicarEntradaActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                            limpiarCajas();
                        } else {
                            Toast.makeText(PublicarEntradaActivity.this, PublicarEntradaActivity.this.getResources().getString(R.string.aviso_error_publicar_entrada), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PublicarEntradaActivity.this, PublicarEntradaActivity.this.getResources().getString(R.string.aviso_error_publicar_entrada), Toast.LENGTH_SHORT).show();
                    }
                dialogoCarga.dismiss();
            } else {
                dialogoCarga.dismiss();
                Toast.makeText(PublicarEntradaActivity.this, PublicarEntradaActivity.this.getResources().getString(R.string.aviso_error_publicar_entrada), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCompleted(String respuesta) {
            procesarResultadoGuardarEntrada(respuesta);
        }
    }
}
