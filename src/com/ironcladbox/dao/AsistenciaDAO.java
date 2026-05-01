package com.ironcladbox.dao;

import com.ironcladbox.model.Asistencia;
import com.ironcladbox.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO implements IAsistenciaDAO {

    @Override
    public Asistencia obtenerPorId(int id) {
        String sql = "SELECT * FROM asistencias WHERE id_asistencia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAsistencia(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Asistencia> obtenerPorAtleta(int idAtleta) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE id_atleta = ? ORDER BY fecha DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAtleta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    asistencias.add(mapResultSetToAsistencia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return asistencias;
    }

    @Override
    public List<Asistencia> obtenerPorClase(int idClase) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE id_clase = ? ORDER BY fecha DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idClase);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    asistencias.add(mapResultSetToAsistencia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return asistencias;
    }

    @Override
    public List<Asistencia> obtenerPorFecha(LocalDate fecha) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE fecha = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    asistencias.add(mapResultSetToAsistencia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return asistencias;
    }

    @Override
    public void guardar(Asistencia asistencia) {
        String sql = "INSERT INTO asistencias (id_atleta, id_clase, fecha, presente) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, asistencia.getIdAtleta());
            stmt.setInt(2, asistencia.getIdClase());
            stmt.setDate(3, Date.valueOf(asistencia.getFecha()));
            stmt.setBoolean(4, asistencia.isPresente());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    asistencia.setIdAsistencia(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Asistencia asistencia) {
        String sql = "UPDATE asistencias SET presente = ? WHERE id_asistencia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, asistencia.isPresente());
            stmt.setInt(2, asistencia.getIdAsistencia());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM asistencias WHERE id_asistencia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Asistencia mapResultSetToAsistencia(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setIdAsistencia(rs.getInt("id_asistencia"));
        asistencia.setIdAtleta(rs.getInt("id_atleta"));
        asistencia.setIdClase(rs.getInt("id_clase"));
        asistencia.setFecha(rs.getDate("fecha").toLocalDate());
        asistencia.setPresente(rs.getBoolean("presente"));
        return asistencia;
    }
}
