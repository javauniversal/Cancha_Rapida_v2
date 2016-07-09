package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import co.zonaapp.emisora.cancharapidav2.Entidades.Login;
import co.zonaapp.emisora.cancharapidav2.Entidades.ResponseReserva;
import co.zonaapp.emisora.cancharapidav2.R;

import static co.zonaapp.emisora.cancharapidav2.Entidades.Login.setLoginStatic;

public class ActMain extends BaseActivity {

    private EditText editUsuario;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editUsuario = (EditText) findViewById(R.id.editUsuario);
        editPassword = (EditText) findViewById(R.id.editPassword);
        TextView txtRecuperar = (TextView) findViewById(R.id.txtRecuperar);

        txtRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.dialog_correo, null);

                final EditText correo = (EditText) dialoglayout.findViewById(R.id.editCorredo);

                AlertDialog.Builder builder = new AlertDialog.Builder(ActMain.this);
                builder.setCancelable(false);
                builder.setTitle("Recuperar Password");
                builder.setView(dialoglayout).setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (correo.getText().toString().equals("")) {
                            //Mensaje
                            Toast.makeText(ActMain.this, "El correo es un campo requerido", Toast.LENGTH_LONG).show();
                        } else {
                            // Enviar respuesta.
                            enviarCorreoPassword(correo.getText().toString());
                        }
                    }
                });

                builder.show();

            }
        });

        Button btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidNumber(editUsuario.getText().toString().trim())) {
                    editUsuario.setError("Campo requerido");
                    editUsuario.requestFocus();
                } else if (isValidNumber(editPassword.getText().toString().trim())){
                    editPassword.setError("Campo requerido");
                    editPassword.requestFocus();
                } else {
                    loginServices();
                }
            }
        });

        Button btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActMain.this, ActRegistrar.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void enviarCorreoPassword(final String s) {
        alertDialog.show();
        String url = String.format("%1$s%2$s", getString(R.string.url_base), "recuperar_password");
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        parseJSONpassword(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(ActMain.this, "Error de tiempo de espera", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(ActMain.this, "Error Servidor", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(ActMain.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(ActMain.this, "Error de red", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ActMain.this, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                        }

                        alertDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", s);
                return params;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jsonRequest);
    }

    private void parseJSONpassword(String response) {
        Gson gson = new Gson();
        if (!response.equals("[]")) {
            final ResponseReserva responseReserva = gson.fromJson(response, ResponseReserva.class);
            if (responseReserva.getId() == -1) {
                Toast.makeText(ActMain.this, responseReserva.getDescripcion(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ActMain.this, responseReserva.getDescripcion(), Toast.LENGTH_LONG).show();
            }
        }
        alertDialog.dismiss();
    }

    private void loginServices() {
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
                            Toast.makeText(ActMain.this, "Error de tiempo de espera", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(ActMain.this, "Error Servidor", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(ActMain.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(ActMain.this, "Error de red", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ActMain.this, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                        }

                        alertDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user", editUsuario.getText().toString().trim());
                params.put("pass", editPassword.getText().toString().trim());

                return params;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jsonRequest);
    }

    private void parseJSON(String response) {
        Gson gson = new Gson();
        if (!response.equals("[]")) {
            try {

                Login login = gson.fromJson(response, Login.class);
                setLoginStatic(login);

                registerUser(this);

                startActivity(new Intent(ActMain.this, ActHome.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            } catch (IllegalStateException ex) {
                ex.printStackTrace();
                alertDialog.dismiss();
            } finally {
                alertDialog.dismiss();
            }
        } else {
            alertDialog.dismiss();
            Toast.makeText(this, "Usuario/Contraseña incorrecta", Toast.LENGTH_LONG).show();
        }
    }

    private void registerUser(Context context){
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(context);
        if (regId.equals("")) {
            GCMRegistrar.register(context, "918001884534");
            GCMRegistrar.setRegisteredOnServer(this, true);
        }
    }

}
