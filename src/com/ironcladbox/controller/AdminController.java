package com.ironcladbox.controller;

import com.ironcladbox.model.*;
import com.ironcladbox.dao.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public void eliminarClase(int idClase) {
        claseDAO.eliminar(idClase);
    }

    public void desactivarUsuario(int idUsuario) {
        Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
        if (usuario != null) {
            usuario.setActivo(false);
            usuarioDAO.actualizar(usuario);
        }
    }

    public void actualizarAtleta(Atleta atleta) {
        atletaDAO.actualizar(atleta);
    }

    public void eliminarAtleta(int idAtleta) {
        atletaDAO.eliminar(idAtleta);
    }

    public void actualizarEntrenador(Entrenador entrenador) {
        entrenadorDAO.actualizar(entrenador);
    }

    public void eliminarEntrenador(int idEntrenador) {
        entrenadorDAO.eliminar(idEntrenador);
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

    public List<Suscripcion> obtenerTodasLasSuscripciones() {
        return suscripcionDAO.obtenerTodas().stream()
                .filter(Suscripcion::isActiva)
                .collect(Collectors.toList());
    }

    public List<Suscripcion> obtenerSuscripcionesDeAtleta(int idAtleta) {
        return suscripcionDAO.obtenerPorAtleta(idAtleta);
    }

    public Suscripcion obtenerSuscripcionActivaDeAtleta(int idAtleta) {
        return suscripcionDAO.obtenerActivaDeAtleta(idAtleta);
    }

    public void crearSuscripcion(int idAtleta, int idMembresia, LocalDate fechaInicio, LocalDate fechaFin) {
        validarRangoFechas(fechaInicio, fechaFin);
        Suscripcion suscripcion = new Suscripcion(idAtleta, idMembresia, fechaInicio, fechaFin);
        suscripcionDAO.guardar(suscripcion);
    }

    public void actualizarSuscripcion(Suscripcion suscripcion) {
        validarRangoFechas(suscripcion.getFechaInicio(), suscripcion.getFechaFin());
        suscripcionDAO.actualizar(suscripcion);
    }

    public void revocarSuscripcion(int idSuscripcion) {
        suscripcionDAO.eliminar(idSuscripcion);
    }

    public void suspenderSuscripcion(int idSuscripcion, LocalDate nuevaFechaFin) {
        Suscripcion suscripcion = suscripcionDAO.obtenerPorId(idSuscripcion);
        if (suscripcion != null) {
            validarRangoFechas(suscripcion.getFechaInicio(), nuevaFechaFin);
            suscripcion.setFechaFin(nuevaFechaFin);
            suscripcionDAO.actualizar(suscripcion);
        }
    }

    public int getTotalSuscripciones() {
        return obtenerTodasLasSuscripciones().size();
    }

    public int getTotalMembresias() {
        return obtenerMembresias().stream()
                .filter(Membresia::isActiva)
                .toList()
                .size();
    }

    private void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Fecha inicio y fin son requeridas");
        }
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new IllegalArgumentException("Fecha fin debe ser posterior a fecha inicio");
        }
    }
}
