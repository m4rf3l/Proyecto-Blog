/*
    PublicarEntradaActivity
    Activity que captura los campos para una nueva entrada y
    llama al consumo de servicios para almacenarla en el servidor.
 */

package com.example.lap.appblog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PublicarEntradaActivity extends AppCompatActivity {

    protected EditText edtTitulo;
    protected EditText edtAutor;
    protected EditText edtContenido;
    protected ProgressDialog dialogoCarga;
    protected boolean publicacionRealizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_entrada);

        edtTitulo = (EditText) findViewById(R.id.edtTitulo);
        edtAutor = (EditText) findViewById(R.id.edtAutor);
        edtContenido = (EditText) findViewById(R.id.edtContenido);

        // Mostrar el botón "back" en el ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        publicacionRealizada = false;
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
                regresar();
                break;
            case R.id.btnPublicar:
                if(validarCajas()) {
                    // Dialogo de confirmación al publicar la entrada
                    AlertDialog.Builder dialogConfirmacion = new AlertDialog.Builder(this);
                    dialogConfirmacion.setMessage(getResources().getString(R.string.confirmacion_publicacion_entrada));
                    dialogConfirmacion.setNegativeButton(getResources().getString(R.string.contenido_negative_button), new DialogInterface.OnClickListener() {
                        // Si el usuario selecciona cancelar
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialogConfirmacion.setPositiveButton(getResources().getString(R.string.contenido_positive_button), new DialogInterface.OnClickListener() {
                        // Si el usuario selecciona publicar
                        public void onClick(DialogInterface dialog, int which) {
                            String titulo = edtTitulo.getText().toString();
                            String autor = edtAutor.getText().toString();
                            String contenido = edtContenido.getText().toString();
                            Calendar calendario = Calendar.getInstance();
                            // Formato de fecha MySQL
                            SimpleDateFormat fechaFormato = new SimpleDateFormat("yyyy-MM-dd");
                            String fecha = fechaFormato.format(calendario.getTime());
                            Entrada entrada = new Entrada(titulo, autor, fecha, contenido);
                            // Llamar al consumo de servicios
                            PublicacionEntrada publicacion = new PublicacionEntrada(entrada);
                            dialogoCarga = ProgressDialog.show(PublicarEntradaActivity.this, "", getResources().getString(R.string.contenido_dialog_publicando_entrada), false);
                            publicacion.hacerPeticion();
                        }
                    });
                    AlertDialog alertDialog = dialogConfirmacion.create();
                    alertDialog.show();
                    // Cambiar color botones del dialog
                    Button botonPositivo = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button botonNegativo = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    botonPositivo.setTextColor(ContextCompat.getColor(this, R.color.green_600));
                    botonNegativo.setTextColor(ContextCompat.getColor(this, R.color.green_600));
                }
                ocultarTeclado();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    // Cachar evento del botón "back" del dispositivo
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            regresar();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    // Método que regresa una bandera al MainActivity que indica si se publicaron nuevas entradas, para actualizar el ListView
    public void regresar() {
        Intent intentResultado = new Intent();
        intentResultado.putExtra("publicacionRealizada", publicacionRealizada);
        setResult(MainActivity.RESULT_OK, intentResultado);
        finish();
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
        edtTitulo.requestFocus();
    }

    public void ocultarTeclado() {
        View view = PublicarEntradaActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*
        PublicacionEntrada
        Clase que hace la petición al consumo de servicios para almacenar la entrada,
        recibe el resultado enviado por el servidor y lo procesa de acuerdo al mismo.
     */
    private class PublicacionEntrada implements OnPostExecute {

        private Entrada entrada;

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
                        String status = respuestaJSON.getString("status");
                        String mensaje = respuestaJSON.getString("msg");
                        // Si el estado de la operación fue 1, entonces la entrada de publicó correctamente
                        if(status.equals("1")) {
                            publicacionRealizada = true;
                            limpiarCajas();
                        }
                        Toast.makeText(PublicarEntradaActivity.this, mensaje, Toast.LENGTH_SHORT).show();
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
