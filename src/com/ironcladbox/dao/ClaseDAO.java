package com.ironcladbox.dao;

import com.ironcladbox.model.Clase;
import com.ironcladbox.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ClaseDAO implements IClaseDAO {

    @Override
    public Clase obtenerPorId(int id) {
        String sql = "SELECT c.*, u.nombre || ' ' || u.apellido as nombre_entrenador FROM clases c " +
                    "JOIN entrenadores e ON c.id_entrenador = e.id_entrenador " +
                    "JOIN usuarios u ON e.id_usuario = u.id_usuario WHERE c.id_clase = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClase(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Clase> obtenerTodas() {
        List<Clase> clases = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre || ' ' || u.apellido as nombre_entrenador FROM clases c " +
                    "JOIN entrenadores e ON c.id_entrenador = e.id_entrenador " +
                    "JOIN usuarios u ON e.id_usuario = u.id_usuario ORDER BY c.dia_semana, c.horario_inicio";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clases.add(mapResultSetToClase(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clases;
    }

    @Override
    public List<Clase> obtenerActivas() {
        List<Clase> clases = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre || ' ' || u.apellido as nombre_entrenador FROM clases c " +
                    "JOIN entrenadores e ON c.id_entrenador = e.id_entrenador " +
                    "JOIN usuarios u ON e.id_usuario = u.id_usuario WHERE c.activa = true ORDER BY c.dia_semana";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clases.add(mapResultSetToClase(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clases;
    }

    @Override
    public List<Clase> obtenerPorEntrenador(int idEntrenador) {
        List<Clase> clases = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre || ' ' || u.apellido as nombre_entrenador FROM clases c " +
                    "JOIN entrenadores e ON c.id_entrenador = e.id_entrenador " +
                    "JOIN usuarios u ON e.id_usuario = u.id_usuario WHERE c.id_entrenador = ? AND c.activa = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEntrenador);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clases.add(mapResultSetToClase(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clases;
    }

    @Override
    public List<Clase> obtenerPorDia(String dia) {
        List<Clase> clases = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre || ' ' || u.apellido as nombre_entrenador FROM clases c " +
                    "JOIN entrenadores e ON c.id_entrenador = e.id_entrenador " +
                    "JOIN usuarios u ON e.id_usuario = u.id_usuario WHERE c.dia_semana = ? AND c.activa = true ORDER BY c.horario_inicio";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clases.add(mapResultSetToClase(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clases;
    }

    @Override
    public void guardar(Clase clase) {
        String sql = "INSERT INTO clases (nombre, descripcion, id_entrenador, horario_inicio, horario_fin, dia_semana, capacidad_maxima, activa) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, clase.getNombre());
            stmt.setString(2, clase.getDescripcion());
            stmt.setInt(3, clase.getIdEntrenador());
            stmt.setTime(4, Time.valueOf(clase.getHorarioInicio()));
            stmt.setTime(5, Time.valueOf(clase.getHorarioFin()));
            stmt.setString(6, clase.getDiaSemana());
            stmt.setInt(7, clase.getCapacidadMaxima());
            stmt.setBoolean(8, clase.isActiva());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    clase.setIdClase(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Clase clase) {
        String sql = "UPDATE clases SET nombre = ?, descripcion = ?, id_entrenador = ?, " +
                    "horario_inicio = ?, horario_fin = ?, dia_semana = ?, capacidad_maxima = ?, activa = ? WHERE id_clase = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clase.getNombre());
            stmt.setString(2, clase.getDescripcion());
            stmt.setInt(3, clase.getIdEntrenador());
            stmt.setTime(4, Time.valueOf(clase.getHorarioInicio()));
            stmt.setTime(5, Time.valueOf(clase.getHorarioFin()));
            stmt.setString(6, clase.getDiaSemana());
            stmt.setInt(7, clase.getCapacidadMaxima());
            stmt.setBoolean(8, clase.isActiva());
            stmt.setInt(9, clase.getIdClase());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "UPDATE clases SET activa = false WHERE id_clase = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Clase mapResultSetToClase(ResultSet rs) throws SQLException {
        Clase clase = new Clase();
        clase.setIdClase(rs.getInt("id_clase"));
        clase.setNombre(rs.getString("nombre"));
        clase.setDescripcion(rs.getString("descripcion"));
        clase.setIdEntrenador(rs.getInt("id_entrenador"));
        clase.setNombreEntrenador(rs.getString("nombre_entrenador"));
        clase.setHorarioInicio(rs.getTime("horario_inicio").toLocalTime());
        clase.setHorarioFin(rs.getTime("horario_fin").toLocalTime());
        clase.setDiaSemana(rs.getString("dia_semana"));
        clase.setCapacidadMaxima(rs.getInt("capacidad_maxima"));
        clase.setActiva(rs.getBoolean("activa"));
        return clase;
    }
}
