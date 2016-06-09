package co.zonaapp.emisora.cancharapidav2.Entidades;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp_gergarga on 08/06/2016.
 */
public class Esenarios {

    @SerializedName("idescenarios")
    private int idescenarios;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("valor")
    private double valor;

    @SerializedName("foto")
    private String foto;

    @SerializedName("estado")
    private int estado;

    public int getIdescenarios() {
        return idescenarios;
    }

    public void setIdescenarios(int idescenarios) {
        this.idescenarios = idescenarios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
