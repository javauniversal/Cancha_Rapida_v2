package co.zonaapp.emisora.cancharapidav2.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.zonaapp.emisora.cancharapidav2.Actividades.ActHome;
import co.zonaapp.emisora.cancharapidav2.Actividades.ActMiReservas;
import co.zonaapp.emisora.cancharapidav2.Entidades.Reservas;
import co.zonaapp.emisora.cancharapidav2.Entidades.ResponseReserva;
import co.zonaapp.emisora.cancharapidav2.R;

public class AdapterReservas extends BaseAdapter {

    private Activity activity;
    private List<Reservas> data;
    public RequestQueue rq;
    public static final String TAG = "MyTag";

    public AdapterReservas(Activity activity, List<Reservas> data) {
        this.activity = activity;
        this.data = data;
        rq = Volley.newRequestQueue(activity);
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }

    @Override
    public Reservas getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.item_list_reservas, null);
            new ViewHolder(convertView);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.txtidReserva.setText(String.format("Código: %1$s", data.get(position).getIdreserva()));
        holder.txtfehca.setText(String.format("Fecha: %1$s", data.get(position).getFecha()));
        holder.txtHora.setText(String.format("Hora: %1$s", data.get(position).getHora()));
        holder.txtValor.setText(String.format("Valor: %1$s", data.get(position).getValor()));
        holder.txtNombre.setText(String.format("%1$s", data.get(position).getNombres()));
        holder.txtApellido.setText(String.format("%1$s", data.get(position).getApellidos()));
        holder.txtEscenario.setText(String.format("%1$s", data.get(position).getEscenario()));

        if (data.get(position).getEstado() == 1) {
            //Por confirmar.
            holder.txtEstado.setText(String.format("%1$s", "Por confirmar"));
            holder.btnAdjuntar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setVisibility(View.VISIBLE);
        } else if (data.get(position).getEstado() == 2) {
            //En espera
            holder.txtEstado.setText(String.format("%1$s", "En espera"));
            holder.btnAdjuntar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setVisibility(View.VISIBLE);
        } else if (data.get(position).getEstado() == 3) {
            //Cancelada
            holder.txtEstado.setText(String.format("%1$s", "Cancelada"));
            holder.btnAdjuntar.setVisibility(View.GONE);
            holder.btnCancelar.setVisibility(View.GONE);
        } else if (data.get(position).getEstado() == 4) {
            //Confirmada
            holder.txtEstado.setText(String.format("%1$s", "Confirmada"));
            holder.btnAdjuntar.setVisibility(View.GONE);
            holder.btnCancelar.setVisibility(View.GONE);
        }

        holder.btnAdjuntar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstado(data.get(position).getIdreserva(), 3);
            }
        });

        return convertView;

    }

    private void cambiarEstado(final int idreserva, final int i) {

        String url = String.format("%1$s%2$s", activity.getString(R.string.url_base), "edit_reserva");
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
                            Toast.makeText(activity, "Error de tiempo de espera", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(activity, "Error Servidor", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(activity, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(activity, "Error de red", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(activity, "Error al serializar los datos", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("idreserva", String.valueOf(idreserva));
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

                Toast.makeText(activity, responseReserva.getDescripcion(), Toast.LENGTH_LONG).show();

                activity.startActivity(new Intent(activity, ActMiReservas.class));
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                activity.finish();

            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            } finally {}
        } else {
            Toast.makeText(activity, "Error: no se recuperaron datos", Toast.LENGTH_LONG).show();
        }
    }

    class ViewHolder {

        TextView txtidReserva;
        TextView txtfehca;
        TextView txtHora;
        TextView txtValor;
        TextView txtNombre;
        TextView txtApellido;
        TextView txtEscenario;
        TextView txtEstado;
        Button btnAdjuntar;
        Button btnCancelar;

        public ViewHolder(View view) {

            txtidReserva = (TextView) view.findViewById(R.id.txtidReserva);
            txtfehca = (TextView) view.findViewById(R.id.txtfehca);
            txtHora = (TextView) view.findViewById(R.id.txtHora);
            txtValor = (TextView) view.findViewById(R.id.txtValor);
            txtNombre = (TextView) view.findViewById(R.id.txtNombre);
            txtApellido = (TextView) view.findViewById(R.id.txtApellido);
            txtEscenario = (TextView) view.findViewById(R.id.txtEscenario);
            txtEstado = (TextView) view.findViewById(R.id.txtEstado);
            btnAdjuntar = (Button) view.findViewById(R.id.btnAdjuntar);
            btnCancelar = (Button) view.findViewById(R.id.btnCancelar);

            view.setTag(this);
        }
    }

}
