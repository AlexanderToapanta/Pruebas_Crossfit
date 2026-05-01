package com.ironcladbox.controller;

import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Rol;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.dao.IUsuarioDAO;
import com.ironcladbox.dao.UsuarioDAO;

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
        if (usuario != null) {
            this.usuarioActual = usuario;
            return true;
        }
        return false;
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
