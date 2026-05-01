package com.ironcladbox.dao;

import com.ironcladbox.model.Entrenador;
import com.ironcladbox.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorDAO implements IEntrenadorDAO {

    @Override
    public Entrenador obtenerPorId(int id) {
        String sql = "SELECT e.*, u.* FROM entrenadores e JOIN usuarios u ON e.id_usuario = u.id_usuario WHERE e.id_entrenador = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntrenador(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Entrenador obtenerPorIdUsuario(int idUsuario) {
        String sql = "SELECT e.*, u.* FROM entrenadores e JOIN usuarios u ON e.id_usuario = u.id_usuario WHERE e.id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntrenador(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Entrenador> obtenerTodos() {
        List<Entrenador> entrenadores = new ArrayList<>();
        String sql = "SELECT e.*, u.* FROM entrenadores e JOIN usuarios u ON e.id_usuario = u.id_usuario WHERE u.activo = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entrenadores.add(mapResultSetToEntrenador(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entrenadores;
    }

    @Override
    public void guardar(Entrenador entrenador) {
        String sql = "INSERT INTO entrenadores (id_usuario, certificacion, especialidad, experiencia_anios) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entrenador.getIdUsuario());
            stmt.setString(2, entrenador.getCertificacion());
            stmt.setString(3, entrenador.getEspecialidad());
            stmt.setInt(4, entrenador.getExperienciaAnios());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entrenador.setIdEntrenador(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Entrenador entrenador) {
        String sql = "UPDATE entrenadores SET certificacion = ?, especialidad = ?, experiencia_anios = ? WHERE id_entrenador = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entrenador.getCertificacion());
            stmt.setString(2, entrenador.getEspecialidad());
            stmt.setInt(3, entrenador.getExperienciaAnios());
            stmt.setInt(4, entrenador.getIdEntrenador());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM entrenadores WHERE id_entrenador = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Entrenador mapResultSetToEntrenador(ResultSet rs) throws SQLException {
        Entrenador entrenador = new Entrenador();
        entrenador.setIdEntrenador(rs.getInt("id_entrenador"));
        entrenador.setIdUsuario(rs.getInt("id_usuario"));
        entrenador.setEmail(rs.getString("email"));
        entrenador.setNombre(rs.getString("nombre"));
        entrenador.setApellido(rs.getString("apellido"));
        entrenador.setTelefono(rs.getString("telefono"));
        entrenador.setCertificacion(rs.getString("certificacion"));
        entrenador.setEspecialidad(rs.getString("especialidad"));
        entrenador.setExperienciaAnios(rs.getInt("experiencia_anios"));
        entrenador.setActivo(rs.getBoolean("activo"));
        return entrenador;
    }
}
