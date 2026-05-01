package com.ironcladbox.model;

public enum Rol {
    ADMINISTRADOR("ADMINISTRADOR"),
    ENTRENADOR("ENTRENADOR"),
    ATLETA("ATLETA");

    private final String nombre;

    Rol(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static Rol fromString(String nombre) {
        for (Rol rol : Rol.values()) {
            if (rol.nombre.equalsIgnoreCase(nombre)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + nombre);
    }
}
