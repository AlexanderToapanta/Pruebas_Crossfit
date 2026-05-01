package com.ironcladbox.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Clase {
    private int idClase;
    private String nombre;
    private String descripcion;
    private int idEntrenador;
    private String nombreEntrenador;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private String diaSemana;
    private int capacidadMaxima;
    private boolean activa;
    private LocalDateTime fechaCreacion;

    public Clase() {
    }

    public Clase(String nombre, String descripcion, int idEntrenador, LocalTime horarioInicio,
                 LocalTime horarioFin, String diaSemana, int capacidadMaxima) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idEntrenador = idEntrenador;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.diaSemana = diaSemana;
        this.capacidadMaxima = capacidadMaxima;
        this.activa = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
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

    public int getIdEntrenador() {
        return idEntrenador;
    }

    public void setIdEntrenador(int idEntrenador) {
        this.idEntrenador = idEntrenador;
    }

    public String getNombreEntrenador() {
        return nombreEntrenador;
    }

    public void setNombreEntrenador(String nombreEntrenador) {
        this.nombreEntrenador = nombreEntrenador;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(LocalTime horarioFin) {
        this.horarioFin = horarioFin;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
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
        return nombre + " - " + diaSemana + " " + horarioInicio + "-" + horarioFin;
    }
}
