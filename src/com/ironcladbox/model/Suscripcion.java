package com.ironcladbox.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Suscripcion {
    private int idSuscripcion;
    private int idAtleta;
    private int idMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;
    private LocalDateTime fechaCreacion;
    private String nombreMembresia;
    private double precioMembresia;

    public Suscripcion() {
    }

    public Suscripcion(int idAtleta, int idMembresia, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idAtleta = idAtleta;
        this.idMembresia = idMembresia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public int getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(int idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public int getIdAtleta() {
        return idAtleta;
    }

    public void setIdAtleta(int idAtleta) {
        this.idAtleta = idAtleta;
    }

    public int getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
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

    public String getNombreMembresia() {
        return nombreMembresia;
    }

    public void setNombreMembresia(String nombreMembresia) {
        this.nombreMembresia = nombreMembresia;
    }

    public double getPrecioMembresia() {
        return precioMembresia;
    }

    public void setPrecioMembresia(double precioMembresia) {
        this.precioMembresia = precioMembresia;
    }

    public boolean isVigente() {
        return activa && LocalDate.now().isBefore(fechaFin);
    }

    @Override
    public String toString() {
        return "Suscripcion{" +
                "idSuscripcion=" + idSuscripcion +
                ", nombreMembresia='" + nombreMembresia + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", vigente=" + isVigente() +
                '}';
    }
}
