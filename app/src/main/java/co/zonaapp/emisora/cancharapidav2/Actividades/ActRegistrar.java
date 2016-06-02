package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.zonaapp.emisora.cancharapidav2.Entidades.Diccionario;
import co.zonaapp.emisora.cancharapidav2.R;

public class ActRegistrar extends BaseActivity {

    private Spinner spinner_tipo_usu;
    private EditText edit_nombre;
    private EditText edit_apellidos;
    private EditText edit_carnet;
    private EditText edit_correo;
    private EditText edit_cell;
    private EditText edit_direccion;
    private EditText edit_password;
    private EditText edit_usuario;
    private int estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edit_nombre = (EditText) findViewById(R.id.edit_nombre);
        edit_apellidos = (EditText) findViewById(R.id.edit_apellidos);
        edit_carnet = (EditText) findViewById(R.id.edit_carnet);
        edit_correo = (EditText) findViewById(R.id.edit_correo);
        edit_cell = (EditText) findViewById(R.id.edit_cell);
        edit_direccion = (EditText) findViewById(R.id.edit_direccion);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_usuario = (EditText) findViewById(R.id.edit_usuario);

        spinner_tipo_usu = (Spinner) findViewById(R.id.spinner_tipo_usu);
        FloatingActionButton guardar_usuario = (FloatingActionButton) findViewById(R.id.guardar_usuario);
        guardar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos();
            }
        });

        tipoUsuario();
    }

    private void validarCampos() {

        if (isValidNumber(edit_nombre.getText().toString().trim())) {
            edit_nombre.setError("Campo requerido");
            edit_nombre.requestFocus();
        } else if (isValidNumber(edit_apellidos.getText().toString().trim())){
            edit_apellidos.setError("Campo requerido");
            edit_apellidos.requestFocus();
        } else if (isValidNumber(edit_carnet.getText().toString().trim())) {
            edit_carnet.setError("Campo requerido");
            edit_carnet.requestFocus();
        } else if (isValidNumber(edit_correo.getText().toString().trim())) {
            edit_correo.setError("Campo requerido");
            edit_correo.requestFocus();
        } else if (isValidNumber(edit_cell.getText().toString().trim())) {
            edit_cell.setError("Campo requerido");
            edit_cell.requestFocus();
        } else if (isValidNumber(edit_direccion.getText().toString().trim())) {
            edit_direccion.setError("Campo requerido");
            edit_direccion.requestFocus();
        } else if (isValidNumber(edit_password.getText().toString().trim())) {
            edit_password.setError("Campo requerido");
            edit_password.requestFocus();
        } else if (isValidNumber(edit_usuario.getText().toString().trim())) {
            edit_usuario.setError("Campo requerido");
            edit_usuario.requestFocus();
        } else {
            guardarUsuario();
        }
    }

    private void guardarUsuario() {
        alertDialog.show();
        String url = String.format("%1$s%2$s", getString(R.string.url_base), "login_user");
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
                            Toast.makeText(ActRegistrar.this, "Error de tiempo de espera", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(ActRegistrar.this, "Error Servidor", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(ActRegistrar.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(ActRegistrar.this, "Error de red", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ActRegistrar.this, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                        }

                        alertDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("edit_nombre", edit_nombre.getText().toString());
                params.put("edit_apellidos", edit_apellidos.getText().toString());
                params.put("edit_carnet", edit_carnet.getText().toString());
                params.put("edit_correo", edit_correo.getText().toString());
                params.put("edit_cell", edit_cell.getText().toString());
                params.put("edit_direccion", edit_direccion.getText().toString());
                params.put("edit_password", edit_password.getText().toString());
                params.put("edit_usuario", edit_usuario.getText().toString());
                params.put("estado", String.valueOf(estado));

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

            } catch (IllegalStateException ex) {
                ex.printStackTrace();
                alertDialog.dismiss();
            } finally {
                alertDialog.dismiss();
            }
        } else {
            alertDialog.dismiss();
            Toast.makeText(this, "Error al Guardar el usuario.", Toast.LENGTH_LONG).show();
        }
    }

    private void tipoUsuario() {
        final List<Diccionario> listaEstados = new ArrayList<>();
        listaEstados.add(new Diccionario(1, "Particular"));
        listaEstados.add(new Diccionario(2, "Estudiante"));

        ArrayAdapter<Diccionario> adapterEstado = new ArrayAdapter<>(this, R.layout.textview_spinner, listaEstados);
        spinner_tipo_usu.setAdapter(adapterEstado);
        spinner_tipo_usu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                estado = listaEstados.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

}
