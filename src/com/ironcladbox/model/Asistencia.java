package com.ironcladbox.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Asistencia {
    private int idAsistencia;
    private int idAtleta;
    private int idClase;
    private LocalDate fecha;
    private boolean presente;
    private LocalDateTime fechaRegistro;

    public Asistencia() {
    }

    public Asistencia(int idAtleta, int idClase, LocalDate fecha, boolean presente) {
        this.idAtleta = idAtleta;
        this.idClase = idClase;
        this.fecha = fecha;
        this.presente = presente;
        this.fechaRegistro = LocalDateTime.now();
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdAtleta() {
        return idAtleta;
    }

    public void setIdAtleta(int idAtleta) {
        this.idAtleta = idAtleta;
    }

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Asistencia{" +
                "idAtleta=" + idAtleta +
                ", idClase=" + idClase +
                ", fecha=" + fecha +
                ", presente=" + presente +
                '}';
    }
}
