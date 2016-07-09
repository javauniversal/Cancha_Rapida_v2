package co.zonaapp.emisora.cancharapidav2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMBaseIntentService;
import java.util.HashMap;
import java.util.Map;
import co.zonaapp.emisora.cancharapidav2.Actividades.ActMiReservas;

public class GCMIntentService extends GCMBaseIntentService {

    private static final int NOTIF_ALERTA_ID = 1;

    public GCMIntentService(){
        super("918001884534");
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String msg = intent.getExtras().getString("price");
        Log.d("GCM", "Mensaje: " + msg);
        notificarMensaje(context, msg);
    }

    @Override
    protected void onError(Context context, String errorId) {
        Log.d("GCM", "Error: " + errorId);
    }

    @Override
    protected void onRegistered(Context context, String regId) {

        String identifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        registrarUsuario(identifier, regId);

    }

    @Override
    protected void onUnregistered(Context context, String s) {
        //Log.d("GCM", "onUnregistered: Desregistrado OK.");
    }

    private void registrarUsuario(final String username, final String regId){
        //String url = "http://zonaapp.co/domidomi/service/notificaciones/";
        String url = String.format("%1$s%2$s", getString(R.string.url_base), "notificaciones/");

        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, "Hola", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tag", "usersave");
                params.put("username", username);
                params.put("gcmcode", regId);
                return params;
            }
        };
        rq.add(jsonRequest);
    }

    private void notificarMensaje(Context context, String msg){

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, ActMiReservas.class), 0);

        long[] vibrate = {100,100,200,300};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Alerta!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bm)
                .setContentTitle("Mensaje de Alerta")
                .setContentText(msg)
                .setContentIntent(pi)
                .setVibrate(vibrate).setAutoCancel(true)
                .setLights(Color.RED, 1, 0)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }

}
