package com.ironcladbox.controller;

import com.ironcladbox.model.Clase;
import com.ironcladbox.model.Asistencia;
import com.ironcladbox.dao.*;
import java.time.LocalDate;
import java.util.List;

public class EntrenadorController {
    private IClaseDAO claseDAO;
    private IAsistenciaDAO asistenciaDAO;

    public EntrenadorController() {
        this.claseDAO = new ClaseDAO();
        this.asistenciaDAO = new AsistenciaDAO();
    }

    public List<Clase> obtenerMisClases(int idEntrenador) {
        return claseDAO.obtenerPorEntrenador(idEntrenador);
    }

    public void registrarAsistencia(int idAtleta, int idClase, boolean presente) {
        Asistencia asistencia = new Asistencia(idAtleta, idClase, LocalDate.now(), presente);
        asistenciaDAO.guardar(asistencia);
    }

    public List<Asistencia> obtenerAsistenciasPorClase(int idClase) {
        return asistenciaDAO.obtenerPorClase(idClase);
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

    public List<Clase> obtenerClasesPorDia(String dia) {
        return claseDAO.obtenerPorDia(dia);
    }
}
