package com.ironcladbox.model;

public class Atleta extends Usuario {
    private int idAtleta;
    private double peso;
    private double altura;

    public Atleta() {
        super();
    }

    public Atleta(String email, String contrasena, String nombre, String apellido, String telefono) {
        super(email, contrasena, nombre, apellido, telefono, Rol.ATLETA);
    }

    public int getIdAtleta() {
        return idAtleta;
    }

    public void setIdAtleta(int idAtleta) {
        this.idAtleta = idAtleta;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double calcularIMC() {
        if (altura <= 0) return 0;
        return peso / (altura * altura);
    }

    @Override
    public String toString() {
        return "Atleta{" +
                "idAtleta=" + idAtleta +
                ", nombre='" + getNombre() + '\'' +
                ", peso=" + peso +
                ", altura=" + altura +
                ", IMC=" + String.format("%.2f", calcularIMC()) +
                '}';
    }
}
