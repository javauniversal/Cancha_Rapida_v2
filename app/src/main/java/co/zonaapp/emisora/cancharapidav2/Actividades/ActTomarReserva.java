package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.text.DecimalFormat;
import static android.Manifest.permission.CAMERA;
import co.zonaapp.emisora.cancharapidav2.Entidades.Esenarios;
import co.zonaapp.emisora.cancharapidav2.R;
import android.view.View;
import android.widget.Toast;

public class ActTomarReserva extends AppCompatActivity implements View.OnClickListener {

    private Bundle bundle;
    private Esenarios esenarios;
    private String fecha;
    private String hora;
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
    private String mPath;
    private CoordinatorLayout coor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomar_reserva);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        format = new DecimalFormat("#,###.##");

        txtFecha = (TextView) findViewById(R.id.txtFecha);
        txtHora = (TextView) findViewById(R.id.txtHora);
        txtCodigo = (TextView) findViewById(R.id.txtCodigo);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcion);
        txtValor = (TextView) findViewById(R.id.txtValor);
        txtNombre = (TextView) findViewById(R.id.txtNombre);
        coor = (CoordinatorLayout) findViewById(R.id.coor);
        btnAdjuntar = (Button) findViewById(R.id.btnAdjuntar);
        btnAdjuntar.setOnClickListener(this);

        Intent intent = this.getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {
            esenarios = (Esenarios) bundle.getSerializable("value");
            fecha = bundle.getString("fecha");
            hora = bundle.getString("hora");

            llenarDatosReserva(esenarios, fecha, hora);
        }

        if(mayRequestStoragePermission())
            btnAdjuntar.setEnabled(true);
        else
            btnAdjuntar.setEnabled(false);

    }

    public void llenarDatosReserva(Esenarios esenarios, String fecha, String hora) {

        txtFecha.setText(String.format("Fecha: %1$s", fecha));
        txtHora.setText(String.format("Hora: %1$s", hora));
        txtCodigo.setText(String.format("Código: %1$s", esenarios.getCodigo()));
        txtNombre.setText(String.format("Nombre: %1$s", esenarios.getNombre()));
        txtDescripcion.setText(String.format("Características: %1$s", esenarios.getDescripcion()));
        txtValor.setText(String.format("Precio: $ %1$s", format.format(esenarios.getValor())));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdjuntar:
                openCamera();
                break;
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
}
