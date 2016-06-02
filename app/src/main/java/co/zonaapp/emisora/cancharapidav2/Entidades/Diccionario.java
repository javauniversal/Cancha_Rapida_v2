package co.zonaapp.emisora.cancharapidav2.Entidades;

import java.io.Serializable;

/**
 * Created by hp_gergarga on 01/06/2016.
 */
public class Diccionario implements Serializable {

    private int id;
    public String descripcion;

    public Diccionario(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
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

    @Override
    public String toString() {
        return this.descripcion;
    }
}
