package co.zonaapp.emisora.cancharapidav2.Entidades;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp_gergarga on 18/06/2016.
 */
public class ResponseReserva {

    @SerializedName("id")
    private int id;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("id_reserva")
    private int id_reserva;

    public int getId_reserva() {
        return id_reserva;
    }

    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
