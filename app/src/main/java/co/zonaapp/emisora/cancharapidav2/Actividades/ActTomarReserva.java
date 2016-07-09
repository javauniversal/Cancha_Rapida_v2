package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static co.zonaapp.emisora.cancharapidav2.Entidades.Login.getLoginStatic;

import co.zonaapp.emisora.cancharapidav2.Calendario.MonthViewThis;
import co.zonaapp.emisora.cancharapidav2.Entidades.Esenarios;
import co.zonaapp.emisora.cancharapidav2.Entidades.ResponseReserva;
import co.zonaapp.emisora.cancharapidav2.R;

import android.widget.TimePicker;
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
import com.github.ik024.calendar_lib.listeners.MonthViewClickListeners;
import com.google.gson.Gson;

public class ActTomarReserva extends BaseActivity implements View.OnClickListener , MonthViewClickListeners {

    private Esenarios esenarios;
    private String fecha = String.valueOf(0);
    private String hora = String.valueOf(0);
    private TextView txtFecha;
    private TextView txtHora;
    private TextView txtCodigo;
    private TextView txtDescripcion;
    private TextView txtValor;
    private TextView txtNombre;
    private DecimalFormat format;
    private Button btnAdjuntar;
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private CoordinatorLayout coor;
    private String mPath;
    private ImageView imgCarnet;
    private Bitmap bitmap;
    private String fileName = "";
    private ProgressDialog prgDialog;
    private String encodedString = "";
    MonthViewThis monthView;

    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomar_reserva);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //getting reference to MonthCalendarView
        monthView = (MonthViewThis) findViewById(R.id.calendar_month_view);

        //registering the click listeners
        monthView.registerClickListener(this);

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        format = new DecimalFormat("#,###.##");

        txtFecha = (TextView) findViewById(R.id.txtFecha);
        txtHora = (TextView) findViewById(R.id.txtHora);
        txtCodigo = (TextView) findViewById(R.id.txtCodigo);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcion);
        txtValor = (TextView) findViewById(R.id.txtValor);
        txtNombre = (TextView) findViewById(R.id.txtNombre);
        imgCarnet = (ImageView) findViewById(R.id.imgCarnet);



        coor = (CoordinatorLayout) findViewById(R.id.coor);
        btnAdjuntar = (Button) findViewById(R.id.btnAdjuntar);
        btnAdjuntar.setOnClickListener(this);

        Button btnconfirmar = (Button) findViewById(R.id.btnconfirmar);
        btnconfirmar.setOnClickListener(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            esenarios = (Esenarios) bundle.getSerializable("value");
            //fecha = bundle.getString("fecha");
            //hora = bundle.getString("hora");

            llenarDatosReserva(esenarios, fecha, hora);
        }

        if(mayRequestStoragePermission())
            btnAdjuntar.setEnabled(true);
        else
            btnAdjuntar.setEnabled(false);

        if (getLoginStatic().getCliente_tipo_key() == 1) {
            btnAdjuntar.setEnabled(false);
        }

    }

    public void llenarDatosReserva(Esenarios esenarios, String fecha, String hora) {

        txtFecha.setText(String.format("Fecha: %1$s", fecha));
        txtHora.setText(String.format("Hora: %1$s", hora));
        txtCodigo.setText(String.format("Código: %1$s", esenarios.getCodigo()));
        txtNombre.setText(String.format("Nombre: %1$s", esenarios.getNombre()));
        txtDescripcion.setText(String.format("Características: %1$s", esenarios.getDescripcion()));
        // Descuento

        int descuento = (int) esenarios.getValor() - (int) esenarios.getValor()*getLoginStatic().getDescuento()/100;

        txtValor.setText(String.format("Precio: $ %1$s", format.format(descuento)));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdjuntar:
                openCamera();
                break;
            case R.id.btnconfirmar:

                if (!fecha.equals("0")) {
                    saveReserva();
                } else {
                    Toast.makeText(ActTomarReserva.this, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private void saveReserva() {

        alertDialog.show();
        String url = String.format("%1$s%2$s", getString(R.string.url_base), "crear_reserva");
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
                            Toast.makeText(ActTomarReserva.this, "Error de tiempo de espera", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(ActTomarReserva.this, "Error Servidor", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(ActTomarReserva.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(ActTomarReserva.this, "Error de red", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ActTomarReserva.this, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                        }

                        alertDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("clientes_iduser", String.valueOf(getLoginStatic().getIduser()));
                params.put("escenarios_idescenarios", String.valueOf(esenarios.getIdescenarios()));
                params.put("fecha", fecha);
                params.put("hora", hora);

                // 1 = particular.
                // 2 = estudiante.
                params.put("descuento", String.valueOf(getLoginStatic().getDescuento()));

                params.put("valor", String.valueOf(esenarios.getValor()*getLoginStatic().getDescuento()/100));
                params.put("tipocliente", String.valueOf(getLoginStatic().getCliente_tipo_key()));
                params.put("estado", String.valueOf(1));

                params.put("imei", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

                params.put("imagen", fileName);
                params.put("data", encodedString);

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
                final ResponseReserva responseReserva = gson.fromJson(response, ResponseReserva.class);

                if (responseReserva.getId() == 3) {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Reserva Alerta!");
                    dialogo1.setMessage(responseReserva.getDescripcion());
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            // Llamar Servicio. 2
                            cambiarEstado(responseReserva.getId_reserva(), 2);
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            // Llamar Servicio. 3
                            cambiarEstado(responseReserva.getId_reserva(), 3);
                        }
                    });

                    dialogo1.show();
                } else if (responseReserva.getId() == 2) {
                    // Realizar el direccionamiento a la lista de reservas.

                    Toast.makeText(this, responseReserva.getDescripcion(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ActTomarReserva.this, ActMiReservas.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();

                } else if (responseReserva.getId() == -1) {
                    Toast.makeText(this, responseReserva.getDescripcion(), Toast.LENGTH_LONG).show();
                }

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

    private void cambiarEstado(final int id_reserva, final int i) {
        alertDialog.show();
        String url = String.format("%1$s%2$s", getString(R.string.url_base), "edit_reserva");
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        parseJSONEstado(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(ActTomarReserva.this, "Error de tiempo de espera", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(ActTomarReserva.this, "Error Servidor", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(ActTomarReserva.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(ActTomarReserva.this, "Error de red", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ActTomarReserva.this, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                        }

                        alertDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("idreserva", String.valueOf(id_reserva));
                params.put("estado", String.valueOf(i));

                return params;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jsonRequest);
    }

    private void parseJSONEstado(String response) {
        Gson gson = new Gson();
        if (!response.equals("[]")) {
            try {
                final ResponseReserva responseReserva = gson.fromJson(response, ResponseReserva.class);

                Toast.makeText(this, responseReserva.getDescripcion(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(ActTomarReserva.this, ActHome.class));
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
            Toast.makeText(this, "Error: no se recuperaron datos", Toast.LENGTH_LONG).show();
        }
    }

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    private boolean mayRequestStoragePermission() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(coor, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS) {
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ActTomarReserva.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                btnAdjuntar.setEnabled(true);
            }
        } else {
            showExplanation();
        }
    }

    private void showExplanation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActTomarReserva.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });

                    bitmap = BitmapFactory.decodeFile(mPath);
                    imgCarnet.setImageBitmap(bitmap);

                    String fileNameSegments[] = mPath.split("/");
                    fileName = fileNameSegments[fileNameSegments.length - 1];

                    uploadImage();

                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    imgCarnet.setImageURI(path);
                    break;

            }
        }
    }

    public void uploadImage() {
        // When Image is selected from Gallery
        if (mPath != null && !mPath.isEmpty()) {
            prgDialog.setMessage("Convirtiendo IMAGEN!");
            prgDialog.show();
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
                bitmap = BitmapFactory.decodeFile(mPath, options);
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
                prgDialog.dismiss();
                // Put converted Image string into Async Http Post param
            }
        }.execute(null, null, null);

    }

    @Override
    public void dateClicked(Date dateClicked) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String strDate = sdf.format(dateClicked);

        Date fechaActual = new Date();
        SimpleDateFormat fechaA = new SimpleDateFormat("yyyy/MM/dd");
        String fechaab = fechaA.format(fechaActual);

        String fechavalida = compararFechasConDate(strDate, fechaab);

        if (fechavalida.equals("La Fecha 1 es menor")) {
            Toast.makeText(this, "La reserva no puede ser menor a la fecha actual", Toast.LENGTH_LONG).show();
        } else {
            dialogoHora(strDate);
        }
    }

    private String compararFechasConDate(String fechaSelect, String fechaActual) {
        String resultado="";
        try {
            /**Obtenemos las fechas enviadas en el formato a comparar*/
            SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaDate1 = formateador.parse(fechaSelect);
            Date fechaDate2 = formateador.parse(fechaActual);

            System.out.println("Parametro Date Fecha 1 = "+fechaDate1+"\n" +
                    "Parametro Date fechaActual = "+fechaDate2+"\n");

            if ( fechaDate1.before(fechaDate2) ){
                resultado= "La Fecha 1 es menor";
            }else{
                if ( fechaDate2.before(fechaDate1) ){
                    resultado= "La Fecha 1 es Mayor";
                }else{
                    resultado= "Las Fechas Son iguales";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultado;
    }


    private void dialogoHora(final String strDate) {
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_get_hora, null);

        timePicker = (TimePicker) dialoglayout.findViewById(R.id.timePicker);
        timePicker.clearFocus();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //builder.setTitle("Seleccionar Hora");
        builder.setView(dialoglayout).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int hour   = timePicker.getCurrentHour();

                fecha = strDate;
                hora = String.valueOf(hour);

                txtFecha.setText(String.format("Fecha: %1$s", fecha));
                txtHora.setText(String.format("Hora: %1$s:00", hora));

                dialog.dismiss();

            }
        });

        builder.show();
    }
}
