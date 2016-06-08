package co.zonaapp.emisora.cancharapidav2.Actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import co.zonaapp.emisora.cancharapidav2.R;

public class ActHome extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout reservar_clic;
    private LinearLayout mi_reserva_clic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reservar_clic = (LinearLayout) findViewById(R.id.reservar_clic);
        reservar_clic.setOnClickListener(this);
        mi_reserva_clic = (LinearLayout) findViewById(R.id.mi_reserva_clic);
        mi_reserva_clic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reservar_clic:
                startActivity(new Intent(ActHome.this, ActReservar.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.mi_reserva_clic:
                startActivity(new Intent(ActHome.this, ActMiReservas.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

        }
    }
}
