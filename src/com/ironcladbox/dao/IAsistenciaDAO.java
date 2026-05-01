package com.ironcladbox.dao;

import com.ironcladbox.model.Asistencia;
import java.time.LocalDate;
import java.util.List;

public interface IAsistenciaDAO {
    Asistencia obtenerPorId(int id);
    List<Asistencia> obtenerPorAtleta(int idAtleta);
    List<Asistencia> obtenerPorClase(int idClase);
    List<Asistencia> obtenerPorFecha(LocalDate fecha);
    void guardar(Asistencia asistencia);
    void actualizar(Asistencia asistencia);
    void eliminar(int id);
}
