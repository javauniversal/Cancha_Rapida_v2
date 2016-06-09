package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.zonaapp.emisora.cancharapidav2.Entidades.Diccionario;
import co.zonaapp.emisora.cancharapidav2.Entidades.Esenarios;
import co.zonaapp.emisora.cancharapidav2.Entidades.ListEsenarios;
import co.zonaapp.emisora.cancharapidav2.R;

public class ActReservar extends BaseActivity {

    private Spinner spinner_esenarios;
    private TextView txtCodigo;
    private TextView txtDescripcion;
    private TextView txtValor;
    private DecimalFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        format = new DecimalFormat("#,###.##");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner_esenarios = (Spinner) findViewById(R.id.spinner_esenarios);
        txtCodigo = (TextView) findViewById(R.id.txtCodigo);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcion);
        txtValor = (TextView) findViewById(R.id.txtValor);

        listaEsenarios();

    }

    private void listaEsenarios() {
        alertDialog.show();
        String url = String.format("%1$s%2$s", getString(R.string.url_base), "listEsenarios");
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                            parseJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(ActReservar.this, "Error de tiempo de espera", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(ActReservar.this, "Error Servidor", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(ActReservar.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(ActReservar.this, "Error de red", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ActReservar.this, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                        }

                        alertDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jsonRequest);
    }

    private void parseJSON(String response) {
        Gson gson = new Gson();
        if (!response.equals("[]")) {
            try {
                ListEsenarios listEsenarios =  gson.fromJson(response, ListEsenarios.class);
                loadeEsenarios(listEsenarios);
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
                alertDialog.dismiss();
            } finally {
                alertDialog.dismiss();
            }
        } else {
            alertDialog.dismiss();
            Toast.makeText(this, "Problemas al recuperar los datos", Toast.LENGTH_LONG).show();
        }
    }

    private void loadeEsenarios(final ListEsenarios listEsenarios) {

        ArrayAdapter<Esenarios> adapterEstado = new ArrayAdapter<>(this, R.layout.textview_spinner, listEsenarios);
        spinner_esenarios.setAdapter(adapterEstado);
        spinner_esenarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Esenarios esenarios = listEsenarios.get(position);
                txtCodigo.setText(String.format("Código: %1$s", esenarios.getCodigo()));
                txtDescripcion.setText(String.format("Descripción: %1$s", esenarios.getDescripcion()));
                txtValor.setText(String.format("Valor: $ %1$s", format.format(esenarios.getValor())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }
}
