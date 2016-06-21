package co.zonaapp.emisora.cancharapidav2.Entidades;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp_gergarga on 19/06/2016.
 */
public class Reservas {

    @SerializedName("idreserva")
    private int idreserva;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("hora_ini")
    private String hora;

    @SerializedName("valor")
    private double valor;

    @SerializedName("estado")
    private int estado;

    @SerializedName("nombres")
    private String nombres;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("escenario")
    private String escenario;

    @SerializedName("imagen_estado")
    private int imagen_estado;

    public int getImagen_estado() {
        return imagen_estado;
    }

    public void setImagen_estado(int imagen_estado) {
        this.imagen_estado = imagen_estado;
    }

    public int getIdreserva() {
        return idreserva;
    }

    public void setIdreserva(int idreserva) {
        this.idreserva = idreserva;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEscenario() {
        return escenario;
    }

    public void setEscenario(String escenario) {
        this.escenario = escenario;
    }

}
