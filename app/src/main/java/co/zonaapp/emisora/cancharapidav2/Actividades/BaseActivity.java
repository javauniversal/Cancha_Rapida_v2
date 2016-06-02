package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.zonaapp.emisora.cancharapidav2.R;
import dmax.dialog.SpotsDialog;

/**
 * Created by hp_gergarga on 29/05/2016.
 */
public class BaseActivity extends AppCompatActivity {

    public SpotsDialog alertDialog;
    public RequestQueue rq;
    public static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertDialog = new SpotsDialog(this, R.style.Custom);
        rq = Volley.newRequestQueue(this);

    }

    public boolean isValidNumber(String number) { return number == null || number.length() == 0; }

    public boolean isValidNumberEmail(String number) {
        Pattern Plantilla = null;
        Matcher Resultado = null;
        Plantilla = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
        Resultado = Plantilla.matcher(number);
        return Resultado.find();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (rq != null) {
            rq.cancelAll(TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rq != null) {
            rq.cancelAll(TAG);
        }
    }

}
