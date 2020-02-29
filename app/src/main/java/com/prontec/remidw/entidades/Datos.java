package com.prontec.remidw.entidades;

public class Datos {
    private String telefono, fecha;
    private String temperatura, humedad, co2, amoniaco;
    private Integer galpon, iddato;

    public Datos() {}

    public Datos(String telefono, String fecha, String temperatura, String humedad, String co2, String amoniaco, Integer galpon) {
        this.telefono = telefono;
        this.fecha = fecha;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.co2 = co2;
        this.amoniaco = amoniaco;
        this.galpon = galpon;
    }

    public Integer getIddato() {
        return iddato;
    }

    public void setIddato(Integer iddato) {
        this.iddato = iddato;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getHumedad() {
        return humedad;
    }

    public void setHumedad(String humedad) {
        this.humedad = humedad;
    }

    public String getCo2() {
        return co2;
    }

    public void setCo2(String co2) {
        this.co2 = co2;
    }

    public String getAmoniaco() {
        return amoniaco;
    }

    public void setAmoniaco(String amoniaco) {
        this.amoniaco = amoniaco;
    }

    public Integer getGalpon() {
        return galpon;
    }

    public void setGalpon(Integer galpon) {
        this.galpon = galpon;
    }

}
