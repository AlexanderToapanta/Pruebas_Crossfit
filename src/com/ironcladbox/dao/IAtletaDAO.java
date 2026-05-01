package com.ironcladbox.dao;

import com.ironcladbox.model.Atleta;
import java.util.List;

public interface IAtletaDAO {
    Atleta obtenerPorId(int id);
    Atleta obtenerPorIdUsuario(int idUsuario);
    List<Atleta> obtenerTodos();
    void guardar(Atleta atleta);
    void actualizar(Atleta atleta);
    void eliminar(int id);
}
