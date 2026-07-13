package com.ironcladbox.model;

public class Ejercicio {
    private int idEjercicio;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private boolean activo;

    public Ejercicio() {}

    public Ejercicio(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public int getIdEjercicio() { return idEjercicio; }
    public void setIdEjercicio(int id) { this.idEjercicio = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
