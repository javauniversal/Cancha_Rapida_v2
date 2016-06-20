package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.widget.ListView;
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

import java.util.HashMap;
import java.util.Map;

import co.zonaapp.emisora.cancharapidav2.Adapter.AdapterReservas;
import co.zonaapp.emisora.cancharapidav2.Entidades.ListReservas;
import co.zonaapp.emisora.cancharapidav2.R;
import static co.zonaapp.emisora.cancharapidav2.Entidades.Login.getLoginStatic;

public class ActMiReservas extends BaseActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_reservas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);

        getReservas();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getReservas() {
        alertDialog.show();
        String url = String.format("%1$s%2$s", getString(R.string.url_base), "listar_reserva");
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
                            Toast.makeText(ActMiReservas.this, "Error de tiempo de espera", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(ActMiReservas.this, "Error Servidor", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(ActMiReservas.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(ActMiReservas.this, "Error de red", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ActMiReservas.this, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                        }

                        alertDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("clientes_iduser", String.valueOf(getLoginStatic().getIduser()));

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                String idImei;
                if ( Build.VERSION.SDK_INT >= 23)
                    idImei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                else
                    idImei = telephonyManager.getDeviceId();

                params.put("imei", idImei);

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
                final ListReservas listReservas = gson.fromJson(response, ListReservas.class);
                AdapterReservas adapterReservas = new AdapterReservas(this, listReservas);
                listView.setAdapter(adapterReservas);

            } catch (IllegalStateException ex) {
                ex.printStackTrace();
                alertDialog.dismiss();
            } finally {
                alertDialog.dismiss();
            }
        } else {
            alertDialog.dismiss();
            Toast.makeText(this, "Error: no se recuperaron datos", Toast.LENGTH_LONG).show();
        }
    }

}
