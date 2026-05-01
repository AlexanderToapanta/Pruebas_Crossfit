package com.ironcladbox.dao;

import com.ironcladbox.model.Membresia;
import java.util.List;

public interface IMembresiaDAO {
    Membresia obtenerPorId(int id);
    List<Membresia> obtenerTodas();
    List<Membresia> obtenerActivas();
    void guardar(Membresia membresia);
    void actualizar(Membresia membresia);
    void eliminar(int id);
}
