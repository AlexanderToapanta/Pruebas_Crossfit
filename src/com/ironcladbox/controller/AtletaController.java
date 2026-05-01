package com.ironcladbox.controller;

import com.ironcladbox.model.Clase;
import com.ironcladbox.model.Asistencia;
import com.ironcladbox.model.Suscripcion;
import com.ironcladbox.dao.*;
import java.time.LocalDate;
import java.util.List;

public class AtletaController {
    private IClaseDAO claseDAO;
    private IAsistenciaDAO asistenciaDAO;
    private ISuscripcionDAO suscripcionDAO;

    public AtletaController() {
        this.claseDAO = new ClaseDAO();
        this.asistenciaDAO = new AsistenciaDAO();
        this.suscripcionDAO = new SuscripcionDAO();
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
}
