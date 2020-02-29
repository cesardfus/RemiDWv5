package com.prontec.remidw.entidades;

public class GranjaUser {
    String telefono, nombre;

    public GranjaUser() {}

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

    public GranjaUser(String telefono, String nombre){
        this.nombre = nombre;
        this.telefono = telefono;
    }
}
