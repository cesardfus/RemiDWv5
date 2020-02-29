package com.prontec.remidw.entidades;

public class Granja {
    private String telefono,integracion,nombre,direccion,propietario;
    private Integer cantgalpones;

    public Granja() {}

    public Granja(String telefono, String nombre, String direccion, String propietario, String integracion, Integer cantgalpones) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.direccion = direccion;
        this.propietario = propietario;
        this.integracion = integracion;
        this.cantgalpones = cantgalpones;
    }

    public Granja(String nombre, String direccion, String propietario, String integracion, Integer cantgalpones) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.propietario = propietario;
        this.integracion = integracion;
        this.cantgalpones = cantgalpones;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getIntegracion() {
        return integracion;
    }

    public void setIntegracion(String integracion) {
        this.integracion = integracion;
    }

    public Integer getCantgalpones() {
        return cantgalpones;
    }

    public void setCantgalpones(Integer cantgalpones) {
        this.cantgalpones = cantgalpones;
    }
}
