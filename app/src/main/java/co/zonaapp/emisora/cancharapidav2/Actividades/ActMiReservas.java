package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import co.zonaapp.emisora.cancharapidav2.Adapter.AdapterReservas;
import co.zonaapp.emisora.cancharapidav2.Entidades.ListReservas;
import co.zonaapp.emisora.cancharapidav2.Entidades.ResponseReserva;
import co.zonaapp.emisora.cancharapidav2.R;

import static co.zonaapp.emisora.cancharapidav2.Entidades.ImagenRuta.getId_reserva;
import static co.zonaapp.emisora.cancharapidav2.Entidades.ImagenRuta.getmPath;
import static co.zonaapp.emisora.cancharapidav2.Entidades.Login.getLoginStatic;

public class ActMiReservas extends BaseActivity  {

    private ListView listView;
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private Bitmap bitmap;
    private String fileName = "";
    private String encodedString = "";
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

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

                /*TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                String idImei;
                if ( Build.VERSION.SDK_INT >= 23)
                    idImei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                else
                    idImei = telephonyManager.getDeviceId();

                params.put("imei", idImei);
                */

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:

                    File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
                    boolean isDirectoryCreated = file.exists();

                    if(!isDirectoryCreated)
                        isDirectoryCreated = file.mkdirs();

                    if(isDirectoryCreated) {
                        Long timestamp = System.currentTimeMillis() / 1000;
                        String imageName = timestamp.toString() + ".jpg";

                        MediaScannerConnection.scanFile(this,
                                new String[]{getmPath()}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned " + path + ":");
                                        Log.i("ExternalStorage", "-> Uri = " + uri);
                                    }
                                });

                        bitmap = BitmapFactory.decodeFile(getmPath());

                        String fileNameSegments[] = getmPath().split("/");
                        fileName = fileNameSegments[fileNameSegments.length - 1];

                        uploadImage();
                    }
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    break;

            }
        }
    }

    public void uploadImage() {
        // When Image is selected from Gallery
        if (getmPath() != null && !getmPath().isEmpty()) {
            //prgDialog.setMessage("Convirtiendo IMAGEN!");
            //prgDialog.show();
            // Convert image to String using Base64
            encodeImagetoString();
            // When Image is not selected from Gallery
        }
    }

    // AsyncTask - To convert Image to String
    public void encodeImagetoString() {

        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() { };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(getmPath(), options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                updateImg();
            }
        }.execute(null, null, null);
    }

    private void updateImg() {
        alertDialog.show();
        String url = String.format("%1$s%2$s", getString(R.string.url_base), "adjuntar_imagen");
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        parseJSONIMG(response);
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

                params.put("idreserva", String.valueOf(getId_reserva()));
                params.put("imagen", fileName);
                params.put("data", encodedString);

                return params;
            }
        };

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jsonRequest);
    }

    private void parseJSONIMG(String response) {
        Gson gson = new Gson();
        final ResponseReserva responseReserva = gson.fromJson(response, ResponseReserva.class);

        if (responseReserva.getId() == 1) {

            //OK
            Toast.makeText(this, responseReserva.getDescripcion(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(ActMiReservas.this, ActMiReservas.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        } else {
            //Error
            Toast.makeText(this, responseReserva.getDescripcion(), Toast.LENGTH_LONG).show();
        }
    }
}
