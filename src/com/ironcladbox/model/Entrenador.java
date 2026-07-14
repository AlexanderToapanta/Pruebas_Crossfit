package com.ironcladbox.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Entrenador extends Usuario {
    private int idEntrenador;
    private String certificacion;
    private String especialidad;
    private int experienciaAnios;
    private LocalDateTime fechaContratacion;
    private String biografia;
    private String direccion;
    private LocalDate fechaNacimiento;

    public Entrenador() {
        super();
    }

    public Entrenador(String email, String contrasena, String nombre, String apellido, String telefono) {
        super(email, contrasena, nombre, apellido, telefono, Rol.ENTRENADOR);
        this.fechaContratacion = LocalDateTime.now();
    }

    public int getIdEntrenador() {
        return idEntrenador;
    }

    public void setIdEntrenador(int idEntrenador) {
        this.idEntrenador = idEntrenador;
    }

    public String getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(String certificacion) {
        this.certificacion = certificacion;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public int getExperienciaAnios() {
        return experienciaAnios;
    }

    public void setExperienciaAnios(int experienciaAnios) {
        this.experienciaAnios = experienciaAnios;
    }

    public LocalDateTime getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDateTime fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    @Override
    public String toString() {
        return "Entrenador{" +
                "idEntrenador=" + idEntrenador +
                ", nombre='" + getNombre() + '\'' +
                ", certificacion='" + certificacion + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", experienciaAnios=" + experienciaAnios +
                '}';
    }
}
