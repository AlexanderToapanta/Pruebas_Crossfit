package com.ironcladbox.controller;

import com.ironcladbox.model.*;
import com.ironcladbox.dao.*;
import java.util.List;

public class AdminController {
    private IUsuarioDAO usuarioDAO;
    private IAtletaDAO atletaDAO;
    private IEntrenadorDAO entrenadorDAO;
    private IMembresiaDAO membresiaDAO;
    private IClaseDAO claseDAO;
    private ISuscripcionDAO suscripcionDAO;

    public AdminController() {
        this.usuarioDAO = new UsuarioDAO();
        this.atletaDAO = new AtletaDAO();
        this.entrenadorDAO = new EntrenadorDAO();
        this.membresiaDAO = new MembresiaDAO();
        this.claseDAO = new ClaseDAO();
        this.suscripcionDAO = new SuscripcionDAO();
    }

    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    public List<Atleta> obtenerTodosAtletas() {
        return atletaDAO.obtenerTodos();
    }

    public List<Entrenador> obtenerTodosEntrenadores() {
        return entrenadorDAO.obtenerTodos();
    }

    public List<Membresia> obtenerMembresias() {
        return membresiaDAO.obtenerTodas();
    }

    public List<Clase> obtenerClases() {
        return claseDAO.obtenerTodas();
    }

    public void crearMembresia(Membresia membresia) {
        membresiaDAO.guardar(membresia);
    }

    public void actualizarMembresia(Membresia membresia) {
        membresiaDAO.actualizar(membresia);
    }

    public void crearClase(Clase clase) {
        claseDAO.guardar(clase);
    }

    public void actualizarClase(Clase clase) {
        claseDAO.actualizar(clase);
    }

    public void desactivarUsuario(int idUsuario) {
        Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
        if (usuario != null) {
            usuario.setActivo(false);
            usuarioDAO.actualizar(usuario);
        }
    }

    public int getTotalAtletas() {
        return obtenerTodosAtletas().size();
    }

    public int getTotalEntrenadores() {
        return obtenerTodosEntrenadores().size();
    }

    public int getTotalClases() {
        return obtenerClases().size();
    }
}
