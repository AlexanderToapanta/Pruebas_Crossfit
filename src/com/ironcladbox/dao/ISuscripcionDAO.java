package com.ironcladbox.dao;

import com.ironcladbox.model.Suscripcion;
import java.util.List;

public interface ISuscripcionDAO {
    Suscripcion obtenerPorId(int id);
    Suscripcion obtenerActivaDeAtleta(int idAtleta);
    List<Suscripcion> obtenerPorAtleta(int idAtleta);
    List<Suscripcion> obtenerTodas();
    void guardar(Suscripcion suscripcion);
    void actualizar(Suscripcion suscripcion);
    void eliminar(int id);
}
