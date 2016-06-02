package co.zonaapp.emisora.cancharapidav2.Entidades;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp_gergarga on 29/05/2016.
 */
public class Login {

    @SerializedName("iduser")
    private int iduser;

    @SerializedName("nombres")
    private String nombres;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("carnet_cedula")
    private String carnet_cedula;

    @SerializedName("correo")
    private String correo;

    @SerializedName("celular_telefono")
    private String celular_telefono;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("password")
    private String password;

    @SerializedName("usuario")
    private String usuario;

    @SerializedName("cliente_tipo_key")
    private int cliente_tipo_key;

    public static Login loginStatic;

    public static Login getLoginStatic() {
        return loginStatic;
    }

    public static void setLoginStatic(Login loginStatic) {
        Login.loginStatic = loginStatic;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
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

    public String getCarnet_cedula() {
        return carnet_cedula;
    }

    public void setCarnet_cedula(String carnet_cedula) {
        this.carnet_cedula = carnet_cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular_telefono() {
        return celular_telefono;
    }

    public void setCelular_telefono(String celular_telefono) {
        this.celular_telefono = celular_telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getCliente_tipo_key() {
        return cliente_tipo_key;
    }

    public void setCliente_tipo_key(int cliente_tipo_key) {
        this.cliente_tipo_key = cliente_tipo_key;
    }
}
