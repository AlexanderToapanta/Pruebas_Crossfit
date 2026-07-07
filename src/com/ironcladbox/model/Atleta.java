package com.ironcladbox.model;

import java.time.LocalDate;

public class Atleta extends Usuario {
    private int idAtleta;
    private double peso;
    private double altura;
    private int idMembresia;
    private String nombreMembresia;
    private LocalDate fechaInicioMembresia;
    private LocalDate fechaFinMembresia;
    private boolean membresiaActiva;

    public Atleta() { super(); }
    public Atleta(String email, String contrasena, String nombre, String apellido, String telefono) {
        super(email, contrasena, nombre, apellido, telefono, Rol.ATLETA);
    }

    public int getIdAtleta() { return idAtleta; }
    public void setIdAtleta(int idAtleta) { this.idAtleta = idAtleta; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }
    public int getIdMembresia() { return idMembresia; }
    public void setIdMembresia(int idMembresia) { this.idMembresia = idMembresia; }
    public String getNombreMembresia() { return nombreMembresia; }
    public void setNombreMembresia(String nombreMembresia) { this.nombreMembresia = nombreMembresia; }
    public LocalDate getFechaInicioMembresia() { return fechaInicioMembresia; }
    public void setFechaInicioMembresia(LocalDate fechaInicioMembresia) { this.fechaInicioMembresia = fechaInicioMembresia; }
    public LocalDate getFechaFinMembresia() { return fechaFinMembresia; }
    public void setFechaFinMembresia(LocalDate fechaFinMembresia) { this.fechaFinMembresia = fechaFinMembresia; }
    public boolean isMembresiaActiva() { return membresiaActiva; }
    public void setMembresiaActiva(boolean membresiaActiva) { this.membresiaActiva = membresiaActiva; }

    public String getVigenciaMembresia() {
        if (nombreMembresia == null || nombreMembresia.isEmpty()) return "Sin membresia";
        if (!membresiaActiva) return "Vencida";
        if (fechaFinMembresia != null && fechaFinMembresia.isBefore(LocalDate.now())) return "Vencida";
        return "Vigente";
    }

    public double calcularIMC() {
        if (altura <= 0) return 0;
        return peso / (altura * altura);
    }

    @Override
    public String toString() {
        return "Atleta{id=" + idAtleta + ", nombre=" + getNombre() + ", membresia=" + nombreMembresia + '}';
    }
}
