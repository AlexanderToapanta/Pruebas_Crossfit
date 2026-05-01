package com.ironcladbox.dao;

import com.ironcladbox.model.Entrenador;
import java.util.List;

public interface IEntrenadorDAO {
    Entrenador obtenerPorId(int id);
    Entrenador obtenerPorIdUsuario(int idUsuario);
    List<Entrenador> obtenerTodos();
    void guardar(Entrenador entrenador);
    void actualizar(Entrenador entrenador);
    void eliminar(int id);
}
