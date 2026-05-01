package com.ironcladbox.dao;

import com.ironcladbox.model.Clase;
import java.util.List;

public interface IClaseDAO {
    Clase obtenerPorId(int id);
    List<Clase> obtenerTodas();
    List<Clase> obtenerActivas();
    List<Clase> obtenerPorEntrenador(int idEntrenador);
    List<Clase> obtenerPorDia(String dia);
    void guardar(Clase clase);
    void actualizar(Clase clase);
    void eliminar(int id);
}
