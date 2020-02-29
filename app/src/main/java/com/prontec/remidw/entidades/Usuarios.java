package com.prontec.remidw.entidades;

public class Usuarios {
    private String usuario, password, rol;
    private Integer iduser;

    public Usuarios() {}

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Usuarios(String usuario, String password, String rol) {
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }

}
