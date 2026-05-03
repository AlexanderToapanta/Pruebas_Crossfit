package com.ironcladbox.controller;

import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Rol;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.model.Entrenador;
import com.ironcladbox.model.Administrador;
import com.ironcladbox.dao.IUsuarioDAO;
import com.ironcladbox.dao.UsuarioDAO;
import com.ironcladbox.dao.AtletaDAO;
import com.ironcladbox.dao.EntrenadorDAO;

public class AuthController {
    private static AuthController instance;
    private Usuario usuarioActual;
    private IUsuarioDAO usuarioDAO;

    private AuthController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    public boolean login(String email, String contrasena) {
        Usuario usuario = usuarioDAO.autenticar(email, contrasena);
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }

        // Convertir a la instancia específica según el rol
        this.usuarioActual = convertirAUsuarioEspecifico(usuario);

        // Validar que usuarioActual tenga un rol válido
        if (this.usuarioActual != null && this.usuarioActual.getRol() != null) {
            return true;
        }
        this.usuarioActual = null;
        return false;
    }

    private Usuario convertirAUsuarioEspecifico(Usuario usuarioGenerico) {
        if (usuarioGenerico == null || usuarioGenerico.getRol() == null) {
            return null; // No continuar si no hay rol
        }
        
        switch (usuarioGenerico.getRol()) {
            case ENTRENADOR:
                EntrenadorDAO entrenadorDAO = new EntrenadorDAO();
                Entrenador entrenador = entrenadorDAO.obtenerPorIdUsuario(usuarioGenerico.getIdUsuario());

                // Si encontramos el entrenador en la tabla, asegurarnos de asignar el rol
                if (entrenador != null) {
                    entrenador.setRol(Rol.ENTRENADOR);
                    return entrenador;
                }

                // Si no encontramos el entrenador específico en la tabla, crear uno básico
                entrenador = new Entrenador();
                entrenador.setIdUsuario(usuarioGenerico.getIdUsuario());
                entrenador.setEmail(usuarioGenerico.getEmail());
                entrenador.setContrasena(usuarioGenerico.getContrasena());
                entrenador.setNombre(usuarioGenerico.getNombre());
                entrenador.setApellido(usuarioGenerico.getApellido());
                entrenador.setTelefono(usuarioGenerico.getTelefono());
                entrenador.setRol(Rol.ENTRENADOR); // Asignar explícitamente el rol
                entrenador.setActivo(usuarioGenerico.isActivo());
                return entrenador;
                
            case ATLETA:
                AtletaDAO atletaDAO = new AtletaDAO();
                Atleta atleta = atletaDAO.obtenerPorIdUsuario(usuarioGenerico.getIdUsuario());

                // Si encontramos el atleta en la tabla, asegurarnos de asignar el rol
                if (atleta != null) {
                    atleta.setRol(Rol.ATLETA);
                    return atleta;
                }

                // Si no encontramos el atleta específico en la tabla, crear uno básico
                atleta = new Atleta();
                atleta.setIdUsuario(usuarioGenerico.getIdUsuario());
                atleta.setEmail(usuarioGenerico.getEmail());
                atleta.setContrasena(usuarioGenerico.getContrasena());
                atleta.setNombre(usuarioGenerico.getNombre());
                atleta.setApellido(usuarioGenerico.getApellido());
                atleta.setTelefono(usuarioGenerico.getTelefono());
                atleta.setRol(Rol.ATLETA); // Asignar explícitamente el rol
                atleta.setActivo(usuarioGenerico.isActivo());
                return atleta;
                
            case ADMINISTRADOR:
                // Para administrador, retornar como Usuario genérico pero asegurarse que tenga el rol
                usuarioGenerico.setRol(Rol.ADMINISTRADOR);
                return usuarioGenerico;
                
            default:
                return usuarioGenerico;
        }
    }

    public boolean registrarAtleta(Atleta atleta) {
        try {
            usuarioDAO.guardar(atleta);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Rol getRolActual() {
        return usuarioActual != null ? usuarioActual.getRol() : null;
    }

    public boolean estaAutenticado() {
        return usuarioActual != null;
    }

    public boolean esAdministrador() {
        return estaAutenticado() && usuarioActual.getRol() == Rol.ADMINISTRADOR;
    }

    public boolean esEntrenador() {
        return estaAutenticado() && usuarioActual.getRol() == Rol.ENTRENADOR;
    }

    public boolean esAtleta() {
        return estaAutenticado() && usuarioActual.getRol() == Rol.ATLETA;
    }
}
