package com.ironcladbox.model;

public class Administrador extends Usuario {
    private int idAdministrador;

    public Administrador() {
        super();
    }

    public Administrador(String email, String contrasena, String nombre, String apellido, String telefono) {
        super(email, contrasena, nombre, apellido, telefono, Rol.ADMINISTRADOR);
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "idAdministrador=" + idAdministrador +
                ", nombre='" + getNombre() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
