package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import co.zonaapp.emisora.cancharapidav2.Entidades.Esenarios;
import co.zonaapp.emisora.cancharapidav2.Entidades.ListEsenarios;
import co.zonaapp.emisora.cancharapidav2.R;

public class ActReservar extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Spinner spinner_esenarios;
    private TextView txtCodigo;
    private TextView txtDescripcion;
    private TextView txtValor;
    private DecimalFormat format;
    private Esenarios esenarios;
    private String fecha;
    private NetworkImageView imgEscenario;
    private ImageLoader mImageLoader;

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
        imgEscenario = (NetworkImageView) findViewById(R.id.imgEscenario);
        txtValor = (TextView) findViewById(R.id.txtValor);
        Button btnReservas = (Button) findViewById(R.id.btnReservas);
        btnReservas.setOnClickListener(this);

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

                esenarios = listEsenarios.get(position);

                txtCodigo.setText(String.format("Código: %1$s", esenarios.getCodigo()));
                txtDescripcion.setText(String.format("Descripción: %1$s", esenarios.getDescripcion()));
                txtValor.setText(String.format("Valor: $ %1$s", format.format(esenarios.getValor())));

                mImageLoader = new ImageLoader(rq, new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }

                });

                imgEscenario.setImageUrl(esenarios.getFoto(), mImageLoader);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnReservas:

                Bundle bundle = new Bundle();
                Intent intent = new Intent(this, ActTomarReserva.class);
                bundle.putSerializable("value", esenarios);
                //bundle.putString("fecha", fecha);
                //bundle.putString("hora", hora);
                intent.putExtras(bundle);
                startActivity(intent);

                /*Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        ActReservar.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                */

                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        monthOfYear = (monthOfYear+1);

        Date fechaSeleccionada = converFecha(year, monthOfYear, dayOfMonth);
        Date fechaActual = new Date();

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                ActReservar.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );

        fecha = String.format("%1$s/%2$s/%3$s", year, monthOfYear, dayOfMonth);

        tpd.show(getFragmentManager(), "Timepickerdialog");

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {

        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0"+hourOfDayEnd : ""+hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0"+minuteEnd : ""+minuteEnd;

        String hora = String.format("%1$s:%2$s", hourString, minuteString);

        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, ActTomarReserva.class);
        bundle.putSerializable("value", esenarios);
        bundle.putString("fecha", fecha);
        bundle.putString("hora", hora);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public Date converFecha(int year, int mes, int dia){
        Calendar calendar = new GregorianCalendar(year, mes, dia);
        Date fecha = new Date(calendar.getTimeInMillis());
        return fecha;
    }
}
