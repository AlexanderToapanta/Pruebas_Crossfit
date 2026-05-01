package com.ironcladbox.dao;

import com.ironcladbox.model.Usuario;
import java.util.List;

public interface IUsuarioDAO {
    Usuario obtenerPorEmail(String email);
    Usuario obtenerPorId(int id);
    List<Usuario> obtenerTodos();
    void guardar(Usuario usuario);
    void actualizar(Usuario usuario);
    void eliminar(int id);
    Usuario autenticar(String email, String contrasena);
}
