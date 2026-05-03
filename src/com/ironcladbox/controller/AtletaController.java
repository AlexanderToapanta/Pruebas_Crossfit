package com.ironcladbox.controller;

import com.ironcladbox.model.Clase;
import com.ironcladbox.model.Asistencia;
import com.ironcladbox.model.Suscripcion;
import com.ironcladbox.model.Membresia;
import com.ironcladbox.dao.*;
import java.time.LocalDate;
import java.util.List;

public class AtletaController {
    private IClaseDAO claseDAO;
    private IAsistenciaDAO asistenciaDAO;
    private ISuscripcionDAO suscripcionDAO;
    private IMembresiaDAO membresiaDAO;

    public AtletaController() {
        this.claseDAO = new ClaseDAO();
        this.asistenciaDAO = new AsistenciaDAO();
        this.suscripcionDAO = new SuscripcionDAO();
        this.membresiaDAO = new MembresiaDAO();
    }

    public List<Clase> obtenerClasesDisponibles() {
        return claseDAO.obtenerActivas();
    }

    public List<Clase> obtenerClasesPorDia(String dia) {
        return claseDAO.obtenerPorDia(dia);
    }

    public Suscripcion obtenerSuscripcionActiva(int idAtleta) {
        return suscripcionDAO.obtenerActivaDeAtleta(idAtleta);
    }

    public List<Asistencia> obtenerHistorialAsistencia(int idAtleta) {
        return asistenciaDAO.obtenerPorAtleta(idAtleta);
    }

    public void registrarAsistencia(int idAtleta, int idClase) {
        Asistencia asistencia = new Asistencia(idAtleta, idClase, LocalDate.now(), true);
        asistenciaDAO.guardar(asistencia);
    }

    public double calcularPorcentajeAsistencia(int idAtleta) {
        List<Asistencia> asistencias = asistenciaDAO.obtenerPorAtleta(idAtleta);
        if (asistencias.isEmpty()) return 0;
        long presentes = asistencias.stream().filter(Asistencia::isPresente).count();
        return (presentes * 100.0) / asistencias.size();
    }

    public Membresia obtenerMembresiaActiva(int idAtleta) {
        Suscripcion suscripcion = obtenerSuscripcionActiva(idAtleta);
        if (suscripcion != null) {
            return membresiaDAO.obtenerPorId(suscripcion.getIdMembresia());
        }
        return null;
    }

    public List<Membresia> obtenerMembresiasCambio() {
        return membresiaDAO.obtenerActivas();
    }

    public void renovarMembresia(int idAtleta, int idNuevaMembresia) {
        Suscripcion subscripcionActual = suscripcionDAO.obtenerActivaDeAtleta(idAtleta);
        Membresia membresiaNueva = membresiaDAO.obtenerPorId(idNuevaMembresia);

        if (subscripcionActual != null && membresiaNueva != null) {
            subscripcionActual.setActiva(false);
            suscripcionDAO.actualizar(subscripcionActual);

            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaFin = fechaInicio.plusDays(membresiaNueva.getDuracionDias());

            Suscripcion nuevaSuscripcion = new Suscripcion(idAtleta, idNuevaMembresia, fechaInicio, fechaFin);
            suscripcionDAO.guardar(nuevaSuscripcion);
        }
    }
}
