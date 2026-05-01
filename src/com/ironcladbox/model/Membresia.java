package com.ironcladbox.model;

import java.time.LocalDateTime;

public class Membresia {
    private int idMembresia;
    private String nombre;
    private String descripcion;
    private double precio;
    private int duracionDias;
    private String beneficios;
    private boolean activa;
    private LocalDateTime fechaCreacion;

    public Membresia() {
    }

    public Membresia(String nombre, String descripcion, double precio, int duracionDias, String beneficios) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionDias = duracionDias;
        this.beneficios = beneficios;
        this.activa = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public int getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(int duracionDias) {
        this.duracionDias = duracionDias;
    }

    public String getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(String beneficios) {
        this.beneficios = beneficios;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Membresia{" +
                "idMembresia=" + idMembresia +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", duracionDias=" + duracionDias +
                '}';
    }
}
